package moa.pay;

import static moa.Crons.EVERY_10_MINUTE_FROM_06_TO_23_HOURS;

import jakarta.persistence.EntityManagerFactory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.pay.client.PaymentProperty;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentCancelRequest;
import moa.pay.domain.TossPayment;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentCancelJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JdbcTemplate jdbcTemplate;
    private final TossClient tossClient;
    private final PaymentProperty paymentProperty;

    @Scheduled(cron = EVERY_10_MINUTE_FROM_06_TO_23_HOURS)
    public void launch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(paymentCancelJob(), jobParameters);
    }

    @Bean
    public Job paymentCancelJob() {
        return new JobBuilder("paymentCancelJob", jobRepository)
                .start(regenerateIdempotencyKeyForCancelStep())  // 결제 취소를 위한 멱등키 재생성
                .next(paymentCancelStep())  // 결제 취소
                .build();
    }

    /*
     * 결제 취소의 멱등성을 보장하기 위해 사용하는 멱등 키의 만료기간이 15일이다.
     * 해당 Step 에서는 멱등키 생성 이후 10일 이상 지난 결제 취소 대기중인 결제(Payment)에 대해서,
     * 멱등키를 재생성하는 작업을 수행한다.
     */
    @Bean
    public Step regenerateIdempotencyKeyForCancelStep() {
        return new StepBuilder("regenerateIdempotencyKeyForCancelStep", jobRepository)
                .<TossPayment, TossPayment>chunk(100, transactionManager)
                .reader(regenerateIdempotencyKeyCandidateReader(null))
                .processor(regenerateIdempotencyKey())
                .writer(regeneratedIdempotencyKeyWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<TossPayment> regenerateIdempotencyKeyCandidateReader(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[regenerateIdempotencyKeyCandidateReader] execute [now: {}]", now);
        return new JpaCursorItemReaderBuilder<TossPayment>()
                .name("regenerateIdempotencyKeyCandidateReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT p FROM TossPayment p
                        WHERE p.status = 'PENDING_CANCEL'
                        AND p.cancel.idKeyUpdatedDate < :now
                        """)
                .parameterValues(Map.of("now", now.minusDays(10)))
                .build();
    }

    @Bean
    public ItemProcessor<TossPayment, TossPayment> regenerateIdempotencyKey() {
        return payment -> {
            payment.regenerateIdempotencyKeyForCancel();
            return payment;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<TossPayment> regeneratedIdempotencyKeyWriter(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return chunk -> {
            Timestamp updatedDate = Timestamp.valueOf(now);
            List<? extends TossPayment> tossPayments = chunk.getItems();
            jdbcTemplate.batchUpdate("""
                    UPDATE toss_payment 
                    SET idempotency_key = ?, id_key_updated_date = ?
                    WHERE id = ?
                    """, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    TossPayment payment = tossPayments.get(i);
                    ps.setString(1, payment.getIdempotencyKeyForCancel());
                    ps.setTimestamp(2, updatedDate);
                    ps.setLong(3, payment.getId());
                }

                @Override
                public int getBatchSize() {
                    return tossPayments.size();
                }
            });
        };
    }

    /*
     * 실제로 결제를 취소하는 Step
     * 취소 대기중인 결제들을 모두 결제 취소시킨다.
     */
    @Bean
    public Step paymentCancelStep() {
        return new StepBuilder("paymentCancelStep", jobRepository)
                .<TossPayment, TossPayment>chunk(10, transactionManager)
                .reader(pendingCancelPaymentReader())
                .writer(paymentCancelWriter())
                .build();
    }

    @Bean
    public ItemReader<TossPayment> pendingCancelPaymentReader() {
        return new JpaCursorItemReaderBuilder<TossPayment>()
                .name("pendingCancelPaymentReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM TossPayment p WHERE p.status = 'PENDING_CANCEL'")
                .build();
    }

    @Bean
    public ItemWriter<TossPayment> paymentCancelWriter() {
        return chunk -> {
            for (TossPayment payment : chunk) {
                try {
                    tossClient.cancelPayment(
                            payment.getPaymentKey(),
                            paymentProperty.basicAuth(),
                            payment.getIdempotencyKeyForCancel(),
                            new TossPaymentCancelRequest(payment.getCancel().getReason())
                    );
                    jdbcTemplate.update("""
                            UPDATE toss_payment SET status = 'CANCELED' 
                            WHERE id = ?
                            """, payment.getId());
                } catch (Exception e) {
                    log.error("[결제 취소 배치] 결제 취소 실패", e);
                }
            }
        };
    }
}

package moa.funding;

import static moa.Crons.EVERY_MIDNIGHT;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FundingCancelJobConfig {

    private final JobLauncher jobLauncher;
    private final JdbcTemplate jdbcTemplate;
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManger;

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void launch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(fundingCancelJob(), jobParameters);
    }

    @Bean
    public Job fundingCancelJob() {
        return new JobBuilder("fundingCancelJob", jobRepository)
                .start(fundingCancelStep(null))
                .build();
    }

    /**
     * 만료된 펀딩 중 7일 이상 지난 펀딩에 대해 결제 취소 상태로 변경하는 작업을 수행한다.
     * <p>
     * 00시에 작업이 수행되나 은행 점검시간을 고려해 PENDING_CANCEL로 돌려두고 환불은 PaymentCancelJobConfig으로 책임을 넘긴다.
     * <p>
     * Funding의 상태 EXPIRED -> CANCELED
     * <p>
     * 참여자의 상태 PARTICIPATING -> CANCEL
     * <p>
     * 결제의 상태 USED, UNUSED -> PENDING_CANCEL
     */
    @Bean
    @JobScope
    public Step fundingCancelStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[만료된 지 일주일 지난 펀딩 취소] 배치작업 수행 time: {}", now);
        return new StepBuilder("fundingCancelStep", jobRepository)
                .<Long, Long>chunk(100, transactionManger)
                .reader(fundingToBeCancelReader(null))
                .writer(fundingCancelWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Long> fundingToBeCancelReader(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        LocalDateTime limitDate = now.minusDays(8);  // 8이어야 7일부터 가능
        return new JdbcCursorItemReaderBuilder<Long>()
                .fetchSize(100)
                .dataSource(dataSource)
                .rowMapper(new SingleColumnRowMapper<>())
                .sql("""
                        SELECT id
                        FROM funding f
                        WHERE f.status = 'EXPIRED'
                        AND f.end_date <= ?
                        """)
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(
                        new Object[]{limitDate}
                ))
                .name("jdbcCursorItemReader")
                .build();
    }

    public ItemWriter<Long> fundingCancelWriter() {
        return chunk -> {
            String fundingIdsInParam = chunk.getItems()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            jdbcTemplate.update("""
                    UPDATE funding f
                    SET f.status = 'CANCELLED'
                    WHERE f.id IN (?);
                    """, fundingIdsInParam
            );
            jdbcTemplate.update("""
                    UPDATE funding_participant fp
                    SET fp.status = 'CANCEL'
                    WHERE fp.funding_id IN (?);
                    """, fundingIdsInParam
            );
            jdbcTemplate.update("""                       
                    UPDATE toss_payment tp
                    SET tp.status = 'PENDING_CANCEL'
                    WHERE tp.id IN (
                                SELECT tp.id
                                FROM toss_payment tp
                                JOIN funding_participant fp ON tp.id = fp.payment_id
                                WHERE fp.funding_id IN (?)
                    ); 
                    """, fundingIdsInParam
            );
        };
    }
}

package moa.funding;

import java.time.LocalDateTime;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchFundingConfig {

    private static final int CHUNK_SIZE = 10;
    public static final String JOB_NAME = "fundingExpireJob";
    private static final String STEP_NAME = "fundingExpireStep";

    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean(JOB_NAME)
    public Job fundingExpireJob(
            Step step
    ) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step)
                .build();
    }

    @JobScope
    @Bean(STEP_NAME)
    public Step paymentCancelStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, transactionManager)
                .reader(expiredFundingReader())  // 데이터 읽어오기 (DB, API)
                .writer(fundingExpireWrite(now))  // DB 에 쓰기
                .build();
    }

    @Bean
    public ItemReader<Long> expiredFundingReader() {
        return new JdbcCursorItemReaderBuilder<Long>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new SingleColumnRowMapper<>())
                .sql("""
                        SELECT id
                        FROM funding f
                        WHERE status = 'PROCESSING'
                        AND end_date < CURRENT_TIMESTAMP
                        """)
                .name("pendingCancelPaymentReader")
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<Long> fundingExpireWrite(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[기간이 지난 펀딩 만료] 배치작업 수행 time: {}", now);
        JdbcBatchItemWriter<Long> writer = new JdbcBatchItemWriterBuilder<Long>()
                .dataSource(dataSource)
                .sql("UPDATE funding SET status = 'EXPIRED' WHERE id = ?")
                .itemPreparedStatementSetter((item, ps) -> ps.setLong(1, item))
                .assertUpdates(false)
                .build();
        writer.afterPropertiesSet();
        return writer;
    }
}

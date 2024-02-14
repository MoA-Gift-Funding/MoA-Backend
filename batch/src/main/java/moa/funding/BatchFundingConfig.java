package moa.funding;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchFundingConfig {

    public static final String JOB_NAME = "fundingExpireJob";

    private final JdbcTemplate jdbcTemplate;
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

    @Bean
    @JobScope
    public Step updateExpiredFundingStatus(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[기간이 지난 펀딩 만료] 배치작업 수행 time: {}", now);
        return new StepBuilder("updateExpiredFundingStatus", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int updated = jdbcTemplate.update("""
                            UPDATE funding SET status = 'EXPIRED' 
                            WHERE status = 'PROCESSING'
                            AND end_date <= ?
                            """, now.minusDays(1)
                    );
                    log.info("{}개의 펀딩 만료 처리 완료", updated);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}

package moa.funding;

import static moa.Crons.EVERY_MIDNIGHT;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.notification.application.NotificationService;
import moa.notification.domain.NotificationFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FundingExpireJobConfig {

    private final JobLauncher jobLauncher;
    private final JdbcTemplate jdbcTemplate;
    private final JobRepository jobRepository;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void launch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(fundingExpireJob(), jobParameters);
    }

    @Bean
    public Job fundingExpireJob() {
        return new JobBuilder("fundingExpireJob", jobRepository)
                .start(fundingExpireStep(null))
                .build();
    }

    /**
     * endDate가 지난 펀딩에 대해 만료 처리를 수행한다.
     */
    @Bean
    @JobScope
    public Step fundingExpireStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[기간이 지난 펀딩 만료] 배치작업 수행 time: {}", now);
        return new StepBuilder("fundingExpireStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int updated = jdbcTemplate.update("""
                            UPDATE funding SET status = 'EXPIRED'
                            WHERE status = 'PROCESSING'
                            AND end_date <= ?
                            """, now.minusDays(1)
                    );
                    log.info("{}개의 펀딩 만료 처리 완료", updated);
                    return FINISHED;
                }, transactionManager)
                .build();
    }
}

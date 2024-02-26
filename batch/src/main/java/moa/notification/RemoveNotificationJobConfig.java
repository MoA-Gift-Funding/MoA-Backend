package moa.notification;


import static moa.Crons.EVERY_MIDNIGHT;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class RemoveNotificationJobConfig {

    private final JobLauncher jobLauncher;
    private final JdbcTemplate jdbcTemplate;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void launch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(removeNotificationJob(), jobParameters);
    }

    /**
     * 15일이 지난 알림들을 모두 제거한다.
     */
    @Bean
    public Job removeNotificationJob() {
        return new JobBuilder("removeNotificationJob", jobRepository)
                .start(removeNotificationStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step removeNotificationStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[15일 지난 알림 제거] 배치작업 수행 time: {}]", now);
        return new StepBuilder("removeNotificationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int updated = jdbcTemplate.update("""
                            DELETE FROM notification
                            WHERE created_date <= ?
                            """, now.minusDays(16));
                    log.info("삭제된 알림 갯수: {}", updated);
                    return FINISHED;
                }, transactionManager)
                .build();
    }
}

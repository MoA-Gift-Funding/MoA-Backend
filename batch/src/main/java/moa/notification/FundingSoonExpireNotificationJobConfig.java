package moa.notification;

import static moa.Crons.EVERY_8PM;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.funding.domain.Funding;
import moa.notification.application.NotificationService;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationFactory;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FundingSoonExpireNotificationJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = EVERY_8PM)
    public void launch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(fundingSoonExpireNotificationJob(), jobParameters);
    }

    @Bean
    public Job fundingSoonExpireNotificationJob() {
        return new JobBuilder("fundingSoonExpireNotificationJob", jobRepository)
                .start(sendNotificationSoonExpireFundingStep(null))
                .build();
    }

    /**
     * 만료 하루 전인 펀딩에 대해 알림을 전송한다.
     */
    @Bean
    @JobScope
    public Step sendNotificationSoonExpireFundingStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[만료 임박 펀딩 알림] 배치작업 수행 time: {}]", now);
        return new StepBuilder("sendNotificationSoonExpireFundingStep", jobRepository)
                .<Funding, Notification>chunk(100, transactionManager)
                .reader(soonExpireNotificationTargetFundingReader(null))
                .processor(soonExpireNotificationTargetFundingProcessor())
                .writer(soonExpireNotificationTargetFundingWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Funding> soonExpireNotificationTargetFundingReader(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return new JpaCursorItemReaderBuilder<Funding>()
                .name("soonExpireNotificationTargetFundingReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT f FROM Funding f
                        WHERE f.endDate = :tomorrow
                        """)
                .parameterValues(Map.of("tomorrow", now.plusDays(1).toLocalDate()))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Funding, Notification> soonExpireNotificationTargetFundingProcessor() {
        return funding -> notificationFactory.generateFundingSoonExpireNotification(
                funding.getTitle(),
                funding.getProduct().getImageUrl(),
                funding.getId(),
                funding.getMember()
        );
    }

    @Bean
    @StepScope
    public ItemWriter<Notification> soonExpireNotificationTargetFundingWriter() {
        return notifications -> {
            try {
                for (Notification notification : notifications) {
                    notificationService.push(notification);
                }
            } catch (Exception e) {
                log.error("[만료 임박 펀딩 알림] 알림 전송 실패", e);
            }
        };
    }
}

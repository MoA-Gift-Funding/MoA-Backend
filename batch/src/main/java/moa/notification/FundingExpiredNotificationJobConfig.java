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
public class FundingExpiredNotificationJobConfig {

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
        jobLauncher.run(fundingExpiredNotificationJob(), jobParameters);
    }


    /**
     * 만료된 펀딩들에 대해, 펀딩의 주인에게 만료되었다는 알림을 전송한다.
     */
    @Bean
    public Job fundingExpiredNotificationJob() {
        return new JobBuilder("fundingExpiredNotificationJob", jobRepository)
                .start(fundingExpiredNotificationStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step fundingExpiredNotificationStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[만료된 펀딩 알림] 배치작업 수행 time: {}]", now);
        return new StepBuilder("fundingExpiredNotificationStep", jobRepository)
                .<Funding, Notification>chunk(100, transactionManager)
                .reader(expiredFundingForNotificationReader(null))
                .processor(expiredFundingForNotificationProcessor())
                .writer(expiredFundingForNotificationWriter())
                .build();
    }

    /**
     * 오늘 만료된 펀딩들(즉, 종료일이 어제인 펀딩들)을 읽어온다.
     * <p/>
     * 해당 배치 작업은 오후 8시에 실행되므로, 오전 00시에 실행되는 FundingExpireJob 으로 인해, 이미 기간이 끝난 펀딩들은 만료된 상태이다.
     */
    @Bean
    @StepScope
    public JpaCursorItemReader<Funding> expiredFundingForNotificationReader(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return new JpaCursorItemReaderBuilder<Funding>()
                .name("expiredFundingForNotificationReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT f FROM Funding f
                        WHERE f.status = 'EXPIRED'
                        AND f.endDate = :yesterday
                        """)
                .parameterValues(Map.of("yesterday", now.minusDays(1).toLocalDate()))
                .build();
    }

    /**
     * 오늘 만료된 펀딩들을, 만료 알림 메세지로 변환한다.
     */
    @Bean
    @StepScope
    public ItemProcessor<Funding, Notification> expiredFundingForNotificationProcessor() {
        return funding -> notificationFactory.generateFundingExpiredNotification(
                funding.getTitle(),
                funding.getProduct().getImageUrl(),
                funding.getId(),
                funding.getMember()
        );
    }

    /**
     * 펀딩 만료 알림 메세지를 전송한다.
     */
    @Bean
    @StepScope
    public ItemWriter<Notification> expiredFundingForNotificationWriter() {
        return notifications -> {
            try {
                for (Notification notification : notifications) {
                    notificationService.push(notification);
                }
            } catch (Exception e) {
                log.error("[만료된 펀딩 알림] 알림 전송 실패", e);
            }
        };
    }
}

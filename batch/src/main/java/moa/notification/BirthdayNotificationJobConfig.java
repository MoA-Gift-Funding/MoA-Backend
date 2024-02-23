package moa.notification;

import static moa.Crons.EVERY_8PM;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.member.domain.Member;
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
public class BirthdayNotificationJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final FriendRepository friendRepository;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = EVERY_8PM)
    public void launch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(birthdayNotificationJob(), jobParameters);
    }

    @Bean
    public Job birthdayNotificationJob() {
        return new JobBuilder("birthdayNotificationJob", jobRepository)
                .start(birthdayNotificationStep(null))
                .build();
    }

    /**
     * 다음날 생일인 친구에 대한 알림을 전송한다.
     */
    @Bean
    @JobScope
    public Step birthdayNotificationStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[친구 생일 알림] 배치작업 수행 time: {}]", now);
        return new StepBuilder("birthdayNotificationStep", jobRepository)
                .<Member, Member>chunk(100, transactionManager)
                .reader(birthdayNotificationReader(null))
                .writer(birthdayNotificationWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Member> birthdayNotificationReader(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        LocalDateTime targetDate = now.plusDays(1);
        return new JpaCursorItemReaderBuilder<Member>()
                .name("birthdayNotificationReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT m FROM Member m
                        WHERE m.birthyear = :year
                        AND m.birthday = :day
                        """)
                .parameterValues(Map.of(
                        "year", now.getYear(),
                        "day", String.format("%02d%d", targetDate.getMonthValue(), targetDate.getDayOfMonth())
                ))
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Member> birthdayNotificationWriter() {
        return members -> {
            for (Member member : members) {
                List<Friend> friendsUnblockedOwner = friendRepository.findUnblockedByTargetId(member);
                List<Friend> ownersUnblockedFriends = friendRepository.findUnblockedByMemberId(member);
                List<Member> unblockedFriends = getUnblockedFriends(friendsUnblockedOwner, ownersUnblockedFriends);
                var notifications = unblockedFriends.stream()
                        .map(notificationFactory::generateBirthdayNotification)
                        .toList();
                for (Notification notification : notifications) {
                    notificationService.push(notification);
                }
            }
        };
    }

    private List<Member> getUnblockedFriends(List<Friend> friendsUnblockedOwner, List<Friend> ownersUnblockedFriends) {
        List<Member> unblocked = friendsUnblockedOwner.stream()
                .map(Friend::getMember)
                .collect(Collectors.toList());
        List<Member> myUnblockedFriendsTarget = ownersUnblockedFriends.stream()
                .map(Friend::getTarget)
                .toList();
        unblocked.retainAll(myUnblockedFriendsTarget);
        return unblocked;
    }
}

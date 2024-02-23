package moa.notification;

import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDateTime;
import moa.BatchTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

@BatchTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class BirthdayNotificationJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job birthdayNotificationJob;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(birthdayNotificationJob);
    }

    @Test
    void 다음날_생일인_사람의_친구들에게_알림_발송() throws Exception {
        // given
        Member member = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member friend = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member friend2 = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        friendRepository.save(new Friend(member, friend, "1"));
        friendRepository.save(new Friend(friend, member, "1"));
        friendRepository.save(new Friend(member, friend2, "1"));
        friendRepository.save(new Friend(friend2, member, "1"));

        // 24년 1월 20일 20시 00분 기준
        LocalDateTime now = LocalDateTime.of(2024, 1, 20, 20, 0, 0);
        setField(member, "birthyear", "2024");
        setField(member, "birthday", "0121");
        memberRepository.save(member);

        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        Integer count = notificationRepository.findAll().size();
        assertThat(count).isEqualTo(2);
    }
}

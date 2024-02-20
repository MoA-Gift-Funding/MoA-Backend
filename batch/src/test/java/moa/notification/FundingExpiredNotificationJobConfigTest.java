package moa.notification;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.ProductFixture.product;
import static moa.funding.domain.FundingStatus.EXPIRED;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import moa.BatchTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.NotificationRepository;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

@BatchTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingExpiredNotificationJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job fundingExpireNotificationJob;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(fundingExpireNotificationJob);
    }

    @Test
    void 어제_만료된_펀딩에_대해_알림_발송() throws Exception {
        // given
        Member owner = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member part = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        friendRepository.save(new Friend(owner, part, "1"));
        friendRepository.save(new Friend(part, owner, "1"));

        // 24년 1월 20일 00시 00분 기준
        LocalDateTime now = LocalDateTime.of(2024, 1, 20, 0, 0, 0);
        var 만료_1일차 = 펀딩_생성(owner, LocalDate.of(2024, 1, 19)); // 알림 발송 대상
        var 만료_2일차 = 펀딩_생성(owner, LocalDate.of(2024, 1, 18));
        var 진행중 = 펀딩_생성(owner, LocalDate.of(2024, 1, 20));
        var 진행중2 = 펀딩_생성(owner, LocalDate.of(2024, 1, 21));

        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        assertThat(notificationRepository.findAll()).hasSize(1);
    }

    private Funding 펀딩_생성(Member owner, LocalDate endDate) {
        Funding funding = funding(
                owner,
                productRepository.save(product("", Price.from("1000000"))),
                "10000",
                endDate
        );
        fundingRepository.save(funding);
        setField(funding, "status", EXPIRED);
        fundingRepository.save(funding);
        return funding;
    }
}

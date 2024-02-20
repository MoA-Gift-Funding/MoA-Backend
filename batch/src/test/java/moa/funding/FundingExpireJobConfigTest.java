package moa.funding;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.ProductFixture.product;
import static moa.funding.domain.FundingStatus.EXPIRED;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import moa.BatchTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingVisibility;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId;
import moa.member.domain.OauthId.OauthProvider;
import moa.notification.domain.NotificationRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
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
class FundingExpireJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job fundingExpireJob;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void 기간이_지난_펀딩의_상태가_EXPIRED가_된다() throws Exception {
        jobLauncherTestUtils.setJob(fundingExpireJob);

        // 24년 1월 4일 00시 기준
        var now = LocalDateTime.of(2024, 1, 4, 0, 0, 0);
        Member member = memberRepository.save(new Member(
                new OauthId("1", OauthProvider.APPLE),
                null,
                "주노",
                "2000",
                "06",
                "testImageUrl",
                "010-1234-5678"
        ));
        Product product = productRepository.save(product("exampleProduct", Price.from("10000")));

        LocalDate nowDate = now.toLocalDate();
        Funding 펀딩_만료_1 = 펀딩_생성(nowDate.minusDays(2), member, product);
        Funding 펀딩_만료_2 = 펀딩_생성(nowDate.minusDays(1), member, product);
        // endDate가 1월 4일인 경우 1월 4일 23:59:59 까지 유효
        // 1월 4일 00시에 실행하면 만료되면 안됨
        Funding 펀딩_정상_1 = 펀딩_생성(nowDate, member, product);
        Funding 펀딩_정상_2 = 펀딩_생성(nowDate.plusDays(1), member, product);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        var expectExpired1 = fundingRepository.getById(펀딩_만료_1.getId()).getStatus();
        var expectExpired2 = fundingRepository.getById(펀딩_만료_2.getId()).getStatus();
        var expectProcess1 = fundingRepository.getById(펀딩_정상_1.getId()).getStatus();
        var expectProcess2 = fundingRepository.getById(펀딩_정상_2.getId()).getStatus();

        assertSoftly(
                softly -> {
                    softly.assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
                    softly.assertThat(expectExpired1).isEqualTo(EXPIRED);
                    softly.assertThat(expectExpired2).isEqualTo(EXPIRED);
                    softly.assertThat(expectProcess1).isEqualTo(PROCESSING);
                    softly.assertThat(expectProcess2).isEqualTo(PROCESSING);
                }
        );
    }

    @Test
    void 만료된_펀딩에_대해_알림_발송() throws Exception {
        // given
        Member owner = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member part = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        friendRepository.save(new Friend(owner, part, "1"));
        friendRepository.save(new Friend(part, owner, "1"));

        // 24년 1월 20일 00시 00분 기준
        LocalDateTime now = LocalDateTime.of(2024, 1, 20, 20, 0, 0);
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


    private Funding 펀딩_생성(LocalDate expireDate, Member member, Product product) {
        return fundingRepository.save(
                new Funding(
                        "testImageUrl",
                        "exampleFundingTitle",
                        "exampleFundingDescription",
                        expireDate,
                        FundingVisibility.PUBLIC,
                        Price.from("15000"),
                        member,
                        product,
                        null,
                        ""
                ));
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

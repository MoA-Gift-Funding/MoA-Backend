package moa.funding;

import static moa.fixture.MemberFixture.member;
import static moa.funding.domain.FundingStatus.CANCELLED;
import static moa.funding.domain.FundingStatus.EXPIRED;
import static moa.funding.domain.MessageVisibility.PUBLIC;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import moa.BatchTest;
import moa.FundingParticipantRepository;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.application.FundingService;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingVisibility;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@BatchTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingCancelJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job fundingCancelJob;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Autowired
    private FundingParticipantRepository fundingParticipantRepository;

    @Autowired
    private FundingService fundingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 만료된지_일주일_된_펀딩의_상태를_변경한다() throws Exception {
        jobLauncherTestUtils.setJob(fundingCancelJob);

        Member owner = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member part = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        friendRepository.save(new Friend(owner, part, "1"));
        friendRepository.save(new Friend(part, owner, "1"));

        // 24년 1월 20일 00시 00분 기준
        LocalDateTime now = LocalDateTime.of(2024, 1, 20, 0, 0, 0);
        var 만료_6일차 = 펀딩_및_참여정보_사전_생성(owner, part, LocalDate.of(2024, 1, 14));
        var 만료_7일차 = 펀딩_및_참여정보_사전_생성(owner, part, LocalDate.of(2024, 1, 13));
        var 만료_8일차 = 펀딩_및_참여정보_사전_생성(owner, part, LocalDate.of(2024, 1, 12)); // 만료 제거 대상
        만료_6일차.expire();
        fundingRepository.save(만료_6일차);
        만료_7일차.expire();
        fundingRepository.save(만료_7일차);
        만료_8일차.expire();
        fundingRepository.save(만료_8일차);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        만료_6일차 = fundingRepository.getById(만료_6일차.getId());
        만료_7일차 = fundingRepository.getById(만료_7일차.getId());
        만료_8일차 = fundingRepository.getById(만료_8일차.getId());
        취소되지_않은_펀딩_상태_확인(만료_6일차);
        취소되지_않은_펀딩_상태_확인(만료_7일차);
        만료된_일주일_초과_펀딩_상태_확인(만료_8일차);
    }

    @Transactional
    protected Funding 펀딩_및_참여정보_사전_생성(Member owner, Member part1, LocalDate endDate) {
        // given
        Funding funding = new Funding(
                null,
                "펀딩이올시다",
                "",
                endDate,
                FundingVisibility.PUBLIC,
                Price.from(10000L),
                owner,
                productRepository.save(new Product("", Price.from("1000000"))),
                null,
                ""
        );
        fundingRepository.save(funding);
        String orderId = UUID.randomUUID().toString();
        String payKey = UUID.randomUUID().toString();
        tossPaymentRepository.save(
                new TossPayment(payKey, orderId, "order", "10000", part1.getId()));
        var command = new FundingParticipateCommand(funding.getId(), part1.getId(), orderId, "hi", PUBLIC);
        fundingService.participate(command);
        return funding;
    }

    protected void 취소되지_않은_펀딩_상태_확인(Funding funding) {
        var parts = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM funding_participant WHERE status = 'PARTICIPATING' AND funding_id = ?"
                , new Object[]{funding.getId()}
                , Integer.class);
        var payments = jdbcTemplate.queryForObject(
                """
                        SELECT count(*) FROM toss_payment tp
                        INNER JOIN funding_participant fp ON tp.id = fp.payment_id
                        INNER JOIN funding f ON fp.funding_id = f.id
                        WHERE tp.status != 'PENDING_CANCEL' AND f.id = ?
                        """
                , new Object[]{funding.getId()}
                , Integer.class);
        assertSoftly(
                softly -> {
                    softly.assertThat(parts).isNotZero();
                    softly.assertThat(payments).isNotZero();
                    softly.assertThat(funding.getStatus()).isEqualTo(EXPIRED);
                }
        );
    }

    protected void 만료된_일주일_초과_펀딩_상태_확인(Funding funding) {
        var parts = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM funding_participant WHERE status = 'CANCEL' AND funding_id = ?"
                , new Object[]{funding.getId()}
                , Integer.class);
        var payments = jdbcTemplate.queryForObject(
                """
                        SELECT count(*) FROM toss_payment tp
                        INNER JOIN funding_participant fp ON tp.id = fp.payment_id
                        INNER JOIN funding f ON fp.funding_id = f.id
                        WHERE tp.status = 'PENDING_CANCEL' AND f.id = ?
                        """
                , new Object[]{funding.getId()}
                , Integer.class);
        assertSoftly(
                softly -> {
                    softly.assertThat(parts).isNotZero();
                    softly.assertThat(payments).isNotZero();
                    softly.assertThat(funding.getStatus()).isEqualTo(CANCELLED);
                }
        );
    }
}

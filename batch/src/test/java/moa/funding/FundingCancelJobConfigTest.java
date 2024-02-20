package moa.funding;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.ProductFixture.product;
import static moa.fixture.TossPaymentFixture.tossPayment;
import static moa.funding.domain.FundingStatus.CANCELLED;
import static moa.funding.domain.FundingStatus.EXPIRED;
import static moa.funding.domain.MessageVisibility.PUBLIC;
import static moa.funding.domain.ParticipantStatus.CANCEL;
import static moa.funding.domain.ParticipantStatus.PARTICIPATING;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.pay.domain.TossPaymentStatus.PENDING_CANCEL;
import static moa.pay.domain.TossPaymentStatus.USED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import moa.BatchTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.application.FundingService;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingRepository;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

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
    private FundingService fundingService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void 만료된지_일주일_된_펀딩의_상태를_변경한다() throws Exception {
        // given
        jobLauncherTestUtils.setJob(fundingCancelJob);

        Member owner = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member part = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        friendRepository.save(new Friend(owner, part, "1"));
        friendRepository.save(new Friend(part, owner, "1"));

        // 24년 1월 20일 00시 00분 기준
        LocalDateTime now = LocalDateTime.of(2024, 1, 20, 0, 0, 0);
        var 만료_6일차 = 만료된_펀딩_및_참여정보_생성(owner, part, LocalDate.of(2024, 1, 14));
        var 만료_7일차 = 만료된_펀딩_및_참여정보_생성(owner, part, LocalDate.of(2024, 1, 13));
        var 만료_8일차 = 만료된_펀딩_및_참여정보_생성(owner, part, LocalDate.of(2024, 1, 12)); // 만료 제거 대상

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        취소되지_않은_펀딩_상태_확인(만료_6일차.getId());
        취소되지_않은_펀딩_상태_확인(만료_7일차.getId());
        만료된_일주일_초과_펀딩_상태_확인(만료_8일차.getId());
    }

    private Funding 만료된_펀딩_및_참여정보_생성(Member owner, Member participant, LocalDate endDate) {
        // given
        Funding funding = funding(
                owner,
                productRepository.save(product("", Price.from("1000000"))),
                "10000",
                endDate
        );
        fundingRepository.save(funding);
        TossPayment payment = tossPaymentRepository.save(tossPayment("10000", participant.getId()));
        var command = new FundingParticipateCommand(
                funding.getId(),
                participant.getId(),
                payment.getOrderId(),
                "hi",
                PUBLIC
        );
        fundingService.participate(command);
        setField(funding, "status", EXPIRED);
        fundingRepository.save(funding);
        return funding;
    }

    private void 취소되지_않은_펀딩_상태_확인(Long fundingId) {
        transactionTemplate.executeWithoutResult((status) -> {
            Funding find = fundingRepository.getById(fundingId);
            assertThat(find.getStatus()).isNotEqualTo(CANCELLED);
            assertThat(find.getParticipants())
                    .extracting(FundingParticipant::getStatus)
                    .containsOnly(PARTICIPATING);
            for (FundingParticipant participant : find.getParticipants()) {
                assertThat(participant.getTossPayment().getStatus())
                        .isEqualTo(USED);
            }
        });
    }

    private void 만료된_일주일_초과_펀딩_상태_확인(Long fundingId) {
        transactionTemplate.executeWithoutResult((status) -> {
            Funding find = fundingRepository.getById(fundingId);
            assertThat(find.getStatus()).isEqualTo(CANCELLED);
            assertThat(find.getParticipants())
                    .extracting(FundingParticipant::getStatus)
                    .containsOnly(CANCEL);
            for (FundingParticipant participant : find.getParticipants()) {
                assertThat(participant.getTossPayment().getStatus())
                        .isEqualTo(PENDING_CANCEL);
            }
        });
    }
}

package moa.funding.application;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.ProductFixture.product;
import static moa.funding.domain.MessageVisibility.PUBLIC;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.pay.domain.TossPaymentStatus.CANCELED;
import static moa.pay.domain.TossPaymentStatus.PENDING_CANCEL;
import static moa.pay.domain.TossPaymentStatus.USED;
import static moa.pay.exception.TossPaymentExceptionType.TOSS_API_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import moa.ApplicationTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.pay.client.TossClient;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
import moa.pay.exception.TossPaymentException;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("펀딩 파사드 (FundingFacade) 은(는)")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingFacadeTest {

    @Autowired
    private FundingFacade facade;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FriendRepository friendRepository;

    @MockBean
    private TossClient tossClient;

    private Member mallang;
    private Member juno;
    private Member luma;
    private Funding funding;
    private TossPayment junoPayment;
    private TossPayment lumaPayment;

    @BeforeEach
    void setUp() {
        mallang = memberRepository.save(member(null, "말랑", "010-1111-1111", SIGNED_UP));
        juno = memberRepository.save(member(null, "주노", "010-2222-2222", SIGNED_UP));
        luma = memberRepository.save(member(null, "루마", "010-3333-3333", SIGNED_UP));
        friendRepository.save(new Friend(mallang, juno, "주노"));
        friendRepository.save(new Friend(mallang, luma, "루마"));
        friendRepository.save(new Friend(juno, mallang, "말랑"));
        friendRepository.save(new Friend(luma, mallang, "말랑"));
        Product product = productRepository.save(product("상품", Price.from("10000")));
        funding = fundingRepository.save(funding(mallang, product, "10000"));
        junoPayment = tossPaymentRepository.save(new TossPayment("1", "1", "8000원", "8000", juno.getId()));
        lumaPayment = tossPaymentRepository.save(new TossPayment("2", "2", "5000원", "5000", luma.getId()));
    }

    @Test
    void 펀딩_참여_실패_시_결제_취소가_이루어진다() throws InterruptedException {
        // given
        FundingParticipateCommand junoParticipantCommand = new FundingParticipateCommand(
                funding.getId(),
                juno.getId(),
                junoPayment.getOrderId(),
                "말랑 ㅎㅇ",
                PUBLIC
        );
        FundingParticipateCommand lumaParticipantCommand = new FundingParticipateCommand(
                funding.getId(),
                luma.getId(),
                lumaPayment.getOrderId(),
                "말랑 ㅎㅇ",
                PUBLIC
        );
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        List<FundingParticipateCommand> commands = List.of(junoParticipantCommand, lumaParticipantCommand);
        CountDownLatch latch = new CountDownLatch(2);

        // when
        for (FundingParticipateCommand command : commands) {
            executorService.submit(() -> {
                facade.participate(command);
                latch.countDown();
            });
        }
        latch.await();

        // then
        assertThat(tossPaymentRepository.findAll())
                .extracting(TossPayment::getStatus)
                .containsExactlyInAnyOrder(CANCELED, USED);
    }

    @Test
    void 결제_취소_시_예외가_발생하면_기존_데이터는_롤백되고_결제_정보는_취소_대기_상태로_유지된다() throws InterruptedException {
        // given
        FundingParticipateCommand junoParticipantCommand = new FundingParticipateCommand(
                funding.getId(),
                juno.getId(),
                junoPayment.getOrderId(),
                "말랑 ㅎㅇ",
                PUBLIC
        );
        FundingParticipateCommand lumaParticipantCommand = new FundingParticipateCommand(
                funding.getId(),
                luma.getId(),
                lumaPayment.getOrderId(),
                "말랑 ㅎㅇ",
                PUBLIC
        );
        willThrow(new TossPaymentException(TOSS_API_ERROR))
                .given(tossClient)
                .cancelPayment(any());
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        List<FundingParticipateCommand> commands = List.of(junoParticipantCommand, lumaParticipantCommand);
        CountDownLatch latch = new CountDownLatch(2);

        // when
        for (FundingParticipateCommand command : commands) {
            executorService.submit(() -> {
                try {
                    facade.participate(command);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        TossPayment afterLumaPayment = tossPaymentRepository.getByOrderId(lumaPayment.getOrderId());
        assertThat(tossPaymentRepository.findAll())
                .extracting(TossPayment::getStatus)
                .containsExactlyInAnyOrder(PENDING_CANCEL, USED);
    }
}

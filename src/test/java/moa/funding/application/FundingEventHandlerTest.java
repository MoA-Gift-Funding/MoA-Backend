package moa.funding.application;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.pay.domain.TossPaymentStatus.CANCELED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import moa.fixture.FundingFixture;
import moa.fixture.MemberFixture;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingCancelEvent;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingRepository;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentCancelRequest;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import moa.support.ApplicationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("펀딩 이벤트 핸들러 (FundingEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ApplicationTest
class FundingEventHandlerTest {

    @Autowired
    private FundingEventHandler fundingEventHandler;

    @Autowired
    private TossClient tossClient;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Test
    void 펀딩_취소_이벤트를_받아_참여자들의_결제를_취소한다() {
        // given
        Member member = memberRepository.save(MemberFixture.member(null, "name", "010-1111-1111", SIGNED_UP));
        Member part = memberRepository.save(MemberFixture.member(null, "name2", "010-2222-1111", SIGNED_UP));
        Member part2 = memberRepository.save(MemberFixture.member(null, "name3", "010-3333-1111", SIGNED_UP));
        Product product = productRepository.save(new Product("name", Price.from("100000")));
        Funding funding = fundingRepository.save(FundingFixture.funding(member, product, "10000"));
        TossPayment payment1 = tossPaymentRepository.save(new TossPayment("1", "1", "1", "10000", part.getId()));
        TossPayment payment2 = tossPaymentRepository.save(new TossPayment("2", "2", "2", "10000", part.getId()));
        funding.participate(new FundingParticipant(part, funding, payment1, "hi"));
        funding.participate(new FundingParticipant(part2, funding, payment2, "hi"));
        fundingRepository.save(funding);

        // when
        fundingEventHandler.cancelFunding(new FundingCancelEvent(funding.getId()));

        // then
        verify(tossClient, times(2))
                .cancelPayment(
                        any(String.class),
                        any(String.class),
                        any(String.class),
                        any(TossPaymentCancelRequest.class)
                );
        assertThat(tossPaymentRepository.findByOrderId("1").get().getStatus())
                .isEqualTo(CANCELED);
        assertThat(tossPaymentRepository.findByOrderId("2").get().getStatus())
                .isEqualTo(CANCELED);
    }
}

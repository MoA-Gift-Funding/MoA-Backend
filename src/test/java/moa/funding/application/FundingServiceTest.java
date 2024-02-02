package moa.funding.application;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.funding.domain.FundingStatus.DELIVERY_WAITING;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;

import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
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
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;


@ApplicationTest
@RecordApplicationEvents
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("펀딩 서비스 (FundingService) 은(는)")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FundingService fundingService;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Autowired
    private ApplicationEvents events;

    @Test
    void 펀딩_참여_테스트_펀딩_참여시_금액이_모두_충족되면_펀딩_완료_이벤트_발행() {
        // given
        Member owner = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        Member part = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        friendRepository.save(new Friend(owner, part, "1"));
        friendRepository.save(new Friend(part, owner, "1"));
        Funding funding = funding(
                owner,
                productRepository.save(new Product("", Price.from("10000"))),
                "10000"
        );
        fundingRepository.save(funding);
        tossPaymentRepository.save(new TossPayment("key", "1", "order", "10000", part.getId()));
        var command = new FundingParticipateCommand(funding.getId(), part.getId(), "1", "hi");

        // when
        fundingService.participate(command);

        // then
        Funding after = fundingRepository.getById(funding.getId());
        assertThat(after.getStatus()).isEqualTo(DELIVERY_WAITING);
        assertThat(events.stream(FundingFinishEvent.class).count()).isEqualTo(1);
    }
}

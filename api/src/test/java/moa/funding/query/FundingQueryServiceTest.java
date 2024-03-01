package moa.funding.query;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.ProductFixture.product;
import static moa.fixture.TossPaymentFixture.tossPayment;
import static moa.funding.domain.MessageVisibility.PUBLIC;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.List;
import moa.ApplicationTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.application.FundingService;
import moa.funding.application.command.FundingParticipateCancelCommand;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.query.response.FundingMessageResponse;
import moa.funding.query.response.ParticipatedFundingResponse;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.pay.domain.TossPaymentRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("펀딩 조회 서비스 (FundingQueryService) 은(는)")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingQueryServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FundingService fundingService;

    @Autowired
    private FundingQueryService fundingQueryService;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Test
    void 내가_참여한_펀딩을_조회한다() {
        // given
        Product product = productRepository.save(product("상품", Price.from("20000")));
        Member member1 = memberRepository.save(member(null, "사람1", "", SIGNED_UP));
        Member member2 = memberRepository.save(member(null, "사람2", "", SIGNED_UP));
        Member member3 = memberRepository.save(member(null, "사람3", "", SIGNED_UP));
        Member member4 = memberRepository.save(member(null, "사람4", "", SIGNED_UP));
        friendRepository.saveAll(List.of(
                new Friend(member1, member3, "친구3"),
                new Friend(member1, member4, "친구4"),
                new Friend(member2, member3, "친구3"),
                new Friend(member2, member4, "친구4"),
                new Friend(member3, member1, "친구1"),
                new Friend(member3, member2, "친구2"),
                new Friend(member3, member4, "친구4"),
                new Friend(member4, member1, "친구1"),
                new Friend(member4, member2, "친구2"),
                new Friend(member4, member3, "친구3")
        ));
        Funding member1Funding = fundingRepository.save(funding(member1, product, "5000"));
        Funding member2Funding = fundingRepository.save(funding(member2, product, "5000"));
        Funding member3Funding = fundingRepository.save(funding(member3, product, "5000"));
        Funding member4Funding = fundingRepository.save(funding(member4, product, "5000"));
        String orderId = tossPaymentRepository.save(tossPayment("5000", member3.getId())).getOrderId();
        String orderId2 = tossPaymentRepository.save(tossPayment("5000", member3.getId())).getOrderId();
        String orderId3 = tossPaymentRepository.save(tossPayment("5000", member3.getId())).getOrderId();
        String orderId4 = tossPaymentRepository.save(tossPayment("5000", member4.getId())).getOrderId();

        fundingService.participate(new FundingParticipateCommand(
                member1Funding.getId(),
                member3.getId(),
                orderId,
                "ㅊㅋ",
                PUBLIC
        ));
        fundingService.participate(new FundingParticipateCommand(
                member2Funding.getId(),
                member3.getId(),
                orderId2,
                "ㅊㅋ",
                PUBLIC
        ));
        fundingService.participate(new FundingParticipateCommand(
                member1Funding.getId(),
                member3.getId(),
                orderId3,
                "ㅊㅋ",
                PUBLIC
        ));
        fundingService.participate(new FundingParticipateCommand(member1Funding.getId(),
                member4.getId(),
                orderId4,
                "ㅊㅋ",
                PUBLIC
        ));

        // when
        Page<ParticipatedFundingResponse> result = fundingQueryService.findParticipatedFundings(
                member3.getId(),
                PageRequest.of(0, 10, Sort.by(DESC, "createdDate"))
        );

        // then
        assertThat(result)
                .hasSize(3)
                .extracting(ParticipatedFundingResponse::fundingId)
                .containsExactly(member1Funding.getId(), member2Funding.getId(), member1Funding.getId());
    }

    @Test
    void 내가_받은_펀딩_메세지_조회시_현재_참여중인_사람의_메세지만_보여진다() {
        // given
        Product product = productRepository.save(product("상품", Price.from("20000")));
        Member member1 = memberRepository.save(member(null, "사람1", "", SIGNED_UP));
        Member member2 = memberRepository.save(member(null, "사람2", "", SIGNED_UP));
        Member member3 = memberRepository.save(member(null, "사람3", "", SIGNED_UP));
        friendRepository.saveAll(List.of(
                new Friend(member1, member3, "친구3"),
                new Friend(member1, member2, "친구2"),
                new Friend(member2, member1, "친구1"),
                new Friend(member2, member3, "친구3"),
                new Friend(member3, member1, "친구1"),
                new Friend(member3, member2, "친구2")
        ));
        Funding member1Funding = fundingRepository.save(funding(member1, product, "5000"));
        String orderId = tossPaymentRepository.save(tossPayment("5000", member2.getId())).getOrderId();
        String orderId2 = tossPaymentRepository.save(tossPayment("5000", member2.getId())).getOrderId();
        String orderId3 = tossPaymentRepository.save(tossPayment("5000", member3.getId())).getOrderId();

        fundingService.participate(new FundingParticipateCommand(
                member1Funding.getId(),
                member2.getId(),
                orderId,
                "ㅊㅋ1",
                PUBLIC
        ));
        Long participantMember2Id = fundingService.participate(new FundingParticipateCommand(member1Funding.getId(),
                member2.getId(),
                orderId2,
                "ㅊㅋ2",
                PUBLIC
        ));
        fundingService.participate(new FundingParticipateCommand(member1Funding.getId(),
                member3.getId(),
                orderId3,
                "ㅊㅋ3",
                PUBLIC
        ));
        fundingService.participateCancel(new FundingParticipateCancelCommand(
                member2.getId(),
                member1Funding.getId(),
                participantMember2Id
        ));

        // when
        Page<FundingMessageResponse> responses = fundingQueryService.findReceivedMessages(
                member1.getId(),
                PageRequest.of(0, 10, Sort.by(DESC, "createdDate"))
        );

        // then
        assertThat(responses)
                .hasSize(2)
                .extracting(FundingMessageResponse::message)
                .containsExactly("ㅊㅋ3", "ㅊㅋ1");
    }
}

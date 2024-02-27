package moa.order.domain;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.ProductFixture.product;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;

import moa.ApplicationTest;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
@DisplayName("주문 트랜잭션 Repo (OrderTransactionRepository) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class OrderTransactionRepositoryTest {

    @Autowired
    private OrderTransactionRepository orderTransactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FundingRepository fundingRepository;

    @Test
    void 특정_주문의_최신_Tx를_가져온다() {
        // given
        Member member = memberRepository.save(member(null, "", "", SIGNED_UP));
        Product product = productRepository.save(product("name", Price.from("1000")));
        Funding funding1 = fundingRepository.save(funding(member, product, "10000"));
        Funding funding2 = fundingRepository.save(funding(member, product, "10000"));
        Order order1 = orderRepository.save(new Order(funding1));
        Order order2 = orderRepository.save(new Order(funding2));
        OrderTransaction order1_1 = orderTransactionRepository.save(new OrderTransaction(order1));
        OrderTransaction order1_2 = orderTransactionRepository.save(new OrderTransaction(order1));
        OrderTransaction order2_1 = orderTransactionRepository.save(new OrderTransaction(order2));
        OrderTransaction order2_2 = orderTransactionRepository.save(new OrderTransaction(order2));

        // when
        OrderTransaction result = orderTransactionRepository.getLastedByOrder(order1);
        OrderTransaction result2 = orderTransactionRepository.getLastedByOrder(order2);

        // then
        assertThat(result).isEqualTo(order1_2);
        assertThat(result2).isEqualTo(order2_2);
    }
}

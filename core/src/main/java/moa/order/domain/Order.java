package moa.order.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.exception.FundingExceptionType.NO_AUTHORITY_FOR_FUNDING;
import static moa.order.domain.OrderStatus.RECEIVED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.address.domain.Address;
import moa.funding.domain.Funding;
import moa.funding.exception.FundingException;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;
import moa.product.domain.Product;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "orders")
public class Order extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "funding_id")
    private Funding funding;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Embedded
    private Address address;

    @Column
    private String deliveryRequestMessage;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(STRING)
    private OrderStatus status;

    // TODO 이거 윈큐브 상품(or 쿠폰형 상품에 특화된 로직이라 나중에 상품 종류 추가되면 구조 변경)
    private int possibleReissueCouponCount = 3;

    public Order(Funding funding) {
        this.funding = funding;
        this.product = funding.getProduct();
        this.address = funding.getAddress();
        this.deliveryRequestMessage = funding.getDeliveryRequestMessage();
        this.member = funding.getMember();
        this.status = RECEIVED;
    }

    public void validateOwner(Member member) {
        if (!this.member.equals(member)) {
            throw new FundingException(NO_AUTHORITY_FOR_FUNDING);
        }
    }

    // TODO 이거 윈큐브 상품(or 쿠폰형 상품에 특화된 로직이라 나중에 상품 종류 추가되면 구조 변경)
    public void reIssueCoupon(String phoneNumber) {
        address = address.changePhone(phoneNumber);
        this.possibleReissueCouponCount--;
    }
}

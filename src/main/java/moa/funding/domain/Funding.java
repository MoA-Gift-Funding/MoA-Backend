package moa.funding.domain;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.FundingStatus.DELIVERY_WAITING;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.funding.domain.Price.ZERO;
import static moa.funding.exception.FundingExceptionType.DIFFERENT_FROM_REMAIN_AMOUNT;
import static moa.funding.exception.FundingExceptionType.EXCEEDED_POSSIBLE_AMOUNT;
import static moa.funding.exception.FundingExceptionType.INVALID_END_DATE;
import static moa.funding.exception.FundingExceptionType.MAXIMUM_AMOUNT_LESS_THAN_MINIMUM;
import static moa.funding.exception.FundingExceptionType.NOT_PROCESSING;
import static moa.funding.exception.FundingExceptionType.NO_AUTHORITY_FINISH;
import static moa.funding.exception.FundingExceptionType.OWNER_CANNOT_PARTICIPATE;
import static moa.funding.exception.FundingExceptionType.PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT;
import static moa.funding.exception.FundingExceptionType.PRODUCT_PRICE_UNDER_MINIMUM_PRICE;
import static moa.funding.exception.FundingExceptionType.UNDER_MINIMUM_AMOUNT;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.address.domain.DeliveryAddress;
import moa.funding.exception.FundingException;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;
import moa.pay.domain.TossPayment;
import moa.product.domain.Product;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Funding extends RootEntity<Long> {

    private static final Price MINIMUM_AMOUNT = Price.from("5000");

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Size(max = 25)
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(STRING)
    @Column(name = "visible")
    private Visibility visible;

    @Enumerated(STRING)
    @Column(name = "status")
    private FundingStatus status;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "maximum_amount"))
    private Price maximumAmount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "minimum_amount"))
    private Price minimumAmount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress address;

    @Column
    private String deliveryRequestMessage;

    @OneToMany(fetch = LAZY, mappedBy = "funding", cascade = {PERSIST, MERGE})
    private List<FundingParticipant> participants = new ArrayList<>();

    public Funding(
            String title,
            String description,
            LocalDate endDate,
            Visibility visible,
            Price maximumAmount,
            Member member,
            Product product,
            DeliveryAddress address,
            String deliveryRequestMessage
    ) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.visible = visible;
        this.status = PROCESSING;
        this.maximumAmount = maximumAmount;
        this.minimumAmount = MINIMUM_AMOUNT;
        this.member = member;
        this.product = product;
        this.address = address;
        this.deliveryRequestMessage = deliveryRequestMessage;
    }

    public void create() {
        if (product.getPrice().isLessThan(MINIMUM_AMOUNT)) {  // 상품 가격이 최소금액보다 작은 경우
            throw new FundingException(PRODUCT_PRICE_UNDER_MINIMUM_PRICE);
        }

        if (maximumAmount.isLessThan(MINIMUM_AMOUNT)) {  // 펀딩 가능 최대금액이 최소금액보다 작은 경우
            throw new FundingException(MAXIMUM_AMOUNT_LESS_THAN_MINIMUM);
        }

        if (product.getPrice().isLessThan(maximumAmount)) {  // 상품 가격이 펀딩 가능 최대금액보다 작은 경우
            throw new FundingException(PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT);
        }

        if (endDate.isBefore(LocalDate.now())) {  // 종료일이 과거인 경우
            throw new FundingException(INVALID_END_DATE);
        }
    }

    public void participate(Member member, TossPayment payment, String message) {
        if (status != PROCESSING) {  // 펀딩이 진행중이 아닌 경우
            throw new FundingException(NOT_PROCESSING);
        }

        if (this.member.equals(member)) {  // 펀딩 개설자가 참여하려는 경우
            throw new FundingException(OWNER_CANNOT_PARTICIPATE);
        }

        Price amount = payment.getTotalAmount();
        if (possibleMaxAmount().isLessThan(amount)) {  // 펀딩가능 최대금액보다 더 많이 펀딩한 경우
            throw new FundingException(EXCEEDED_POSSIBLE_AMOUNT);
        }

        // 금액이 펀딩 최소금액보다 낮은 경우, 펀딩의 남은 가격과 일치하지 않으면 예외
        if (amount.isLessThan(minimumAmount) && !amount.equals(remainAmount())) {
            throw new FundingException(UNDER_MINIMUM_AMOUNT);
        }

        FundingParticipant fundingParticipant = new FundingParticipant(member, this, payment, message);
        participants.add(fundingParticipant);
        if (product.getPrice().equals(getFundedAmount())) {
            this.status = DELIVERY_WAITING;
            registerEvent(new FundingFinishEvent(id));
        }
    }

    public Price possibleMaxAmount() {
        Price remained = remainAmount();
        if (maximumAmount.isGreaterThan(remained)) {
            return remained;
        }
        return maximumAmount;
    }

    public Price remainAmount() {
        return product.getPrice().minus(getFundedAmount());
    }

    public Price getFundedAmount() {
        return participants.stream()
                .map(FundingParticipant::getAmount)
                .reduce(ZERO, Price::add);
    }

    public void finish(Member member, Price price) {
        if (!this.member.equals(member)) {
            throw new FundingException(NO_AUTHORITY_FINISH);
        }

        if (!remainAmount().equals(price)) {
            throw new FundingException(DIFFERENT_FROM_REMAIN_AMOUNT);
        }

        this.status = DELIVERY_WAITING;
        registerEvent(new FundingFinishEvent(id));
    }

    public int getFundingRate() {
        Price fundedAmount = getFundedAmount();
        return (int) (fundedAmount.divide(product.getPrice()) * 100);
    }
}

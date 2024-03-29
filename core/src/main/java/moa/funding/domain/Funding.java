package moa.funding.domain;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.FundingStatus.CANCELLED;
import static moa.funding.domain.FundingStatus.COMPLETE;
import static moa.funding.domain.FundingStatus.EXPIRED;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.funding.domain.ParticipantStatus.PARTICIPATING;
import static moa.funding.exception.FundingExceptionType.DIFFERENT_FROM_FUNDING_REMAIN_AMOUNT;
import static moa.funding.exception.FundingExceptionType.EXCEEDED_POSSIBLE_FUNDING_AMOUNT;
import static moa.funding.exception.FundingExceptionType.EXCEED_FUNDING_MAX_PERIOD;
import static moa.funding.exception.FundingExceptionType.FUNDING_MAXIMUM_AMOUNT_LESS_THAN_MINIMUM;
import static moa.funding.exception.FundingExceptionType.FUNDING_PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT;
import static moa.funding.exception.FundingExceptionType.FUNDING_PRODUCT_PRICE_UNDER_MINIMUM_PRICE;
import static moa.funding.exception.FundingExceptionType.INVALID_FUNDING_END_DATE;
import static moa.funding.exception.FundingExceptionType.MUST_FUNDING_MORE_THAN_MINIMUM_AMOUNT;
import static moa.funding.exception.FundingExceptionType.NOT_PROCESSING_FUNDING;
import static moa.funding.exception.FundingExceptionType.NO_AUTHORITY_FOR_FUNDING;
import static moa.funding.exception.FundingExceptionType.OWNER_CANNOT_PARTICIPATE_FUNDING;
import static moa.funding.exception.FundingExceptionType.PROCESSING_OR_EXPIRED_FUNDING_CAN_BE_CANCELLED;
import static moa.funding.exception.FundingExceptionType.PROCESSING_OR_EXPIRED_FUNDING_CAN_BE_FINISHED;
import static moa.global.domain.Price.ZERO;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import moa.address.domain.Address;
import moa.funding.exception.FundingException;
import moa.global.domain.Price;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;
import moa.product.domain.Product;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Funding extends RootEntity<Long> {

    private static final Price MINIMUM_AMOUNT = Price.from("5000");

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String imageUrl;

    @Size(max = 25)
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(STRING)
    @Column(nullable = false)
    private FundingVisibility visibility;

    @Enumerated(STRING)
    @Column(nullable = false)
    private FundingStatus status;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "maximum_amount"))
    private Price maximumAmount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "minimum_amount"))
    private Price minimumAmount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "name", column = @Column(name = "address_name"))
            }
    )
    private Address address;

    @Column
    private String deliveryRequestMessage;

    @Enumerated
    @AttributeOverride(name = "value", column = @Column(name = "my_finished_payment_amount"))
    private Price myFinishedPaymentAmount = ZERO;

    @OneToMany(fetch = LAZY, mappedBy = "funding", cascade = {PERSIST, MERGE})
    private List<FundingParticipant> participants = new ArrayList<>();

    public Funding(
            String imageUrl,
            String title,
            String description,
            LocalDate endDate,
            FundingVisibility visibility,
            Price maximumAmount,
            Member member,
            Product product,
            Address address,
            String deliveryRequestMessage
    ) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.visibility = visibility;
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
            throw new FundingException(FUNDING_PRODUCT_PRICE_UNDER_MINIMUM_PRICE);
        }

        if (maximumAmount.isLessThan(MINIMUM_AMOUNT)) {  // 펀딩 가능 최대금액이 최소금액보다 작은 경우
            throw new FundingException(FUNDING_MAXIMUM_AMOUNT_LESS_THAN_MINIMUM);
        }

        if (product.getPrice().isLessThan(maximumAmount)) {  // 상품 가격이 펀딩 가능 최대금액보다 작은 경우
            throw new FundingException(FUNDING_PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT);
        }

        if (endDate.isBefore(LocalDate.now())) {  // 종료일이 과거인 경우
            throw new FundingException(INVALID_FUNDING_END_DATE);
        }

        if (endDate.isAfter(LocalDate.now().plusWeeks(4))) {
            throw new FundingException(EXCEED_FUNDING_MAX_PERIOD);
        }

        registerEvent(new FundingCreateEvent(this));
    }

    public void participate(FundingParticipant participant) {
        if (status != PROCESSING) {  // 펀딩이 진행중이 아닌 경우
            throw new FundingException(NOT_PROCESSING_FUNDING);
        }

        if (this.member.equals(participant.getMember())) {  // 펀딩 개설자가 참여하려는 경우
            throw new FundingException(OWNER_CANNOT_PARTICIPATE_FUNDING);
        }

        Price amount = participant.getAmount();
        if (possibleMaxAmount().isLessThan(amount)) {  // 펀딩가능 최대금액보다 더 많이 펀딩한 경우
            throw new FundingException(EXCEEDED_POSSIBLE_FUNDING_AMOUNT);
        }

        // 금액이 펀딩 최소금액보다 낮은 경우, 펀딩의 남은 가격과 일치하지 않으면 예외
        Price remainAmount = remainAmount();
        if (amount.isLessThan(minimumAmount) && !amount.equals(remainAmount)) {
            throw new FundingException(MUST_FUNDING_MORE_THAN_MINIMUM_AMOUNT);
        }

        participants.add(participant);
        if (participant.getAmount().equals(remainAmount)) {
            this.status = COMPLETE;
            registerEvent(new FundingFinishEvent(id));
        }
        registerEvent(new FundingParticipateEvent(id, participant));
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
                .filter(it -> it.getStatus().equals(PARTICIPATING))
                .map(FundingParticipant::getAmount)
                .reduce(myFinishedPaymentAmount, Price::add);
    }

    public void validateOwner(Member member) {
        if (!this.member.equals(member)) {
            throw new FundingException(NO_AUTHORITY_FOR_FUNDING);
        }
    }

    public void finish(Price price) {
        if (status != PROCESSING && status != EXPIRED) {
            throw new FundingException(PROCESSING_OR_EXPIRED_FUNDING_CAN_BE_FINISHED);
        }

        if (!remainAmount().equals(price)) {
            throw new FundingException(DIFFERENT_FROM_FUNDING_REMAIN_AMOUNT);
        }

        this.status = COMPLETE;
        myFinishedPaymentAmount = price;
        registerEvent(new FundingFinishEvent(id));
    }

    public void cancel() {
        if (status != PROCESSING && status != EXPIRED) {
            throw new FundingException(PROCESSING_OR_EXPIRED_FUNDING_CAN_BE_CANCELLED);
        }
        this.status = CANCELLED;
        for (FundingParticipant participant : participants) {
            if (participant.isParticipating()) {
                participant.canceledByFundingOwner();
            }
        }
        registerEvent(new FundingCancelEvent(id));
    }

    public int getFundingRate() {
        Price fundedAmount = getFundedAmount();
        return (int) (fundedAmount.divide(product.getPrice()) * 100);
    }

    public List<FundingParticipant> getParticipatingParticipants() {
        return participants.stream()
                .filter(FundingParticipant::isParticipating)
                .toList();
    }
}

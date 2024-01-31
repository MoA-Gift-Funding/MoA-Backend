package moa.funding.domain;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.funding.domain.Price.ZERO;
import static moa.funding.exception.FundingExceptionType.EXCEEDED_POSSIBLE_AMOUNT;
import static moa.funding.exception.FundingExceptionType.INVALID_END_DATE;
import static moa.funding.exception.FundingExceptionType.MAXIMUM_AMOUNT_LESS_THAN_MINIMUM;
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

    @OneToMany(fetch = LAZY, mappedBy = "funding", cascade = {PERSIST})
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
        if (product.getPrice().isLessThan(MINIMUM_AMOUNT)) {
            throw new FundingException(PRODUCT_PRICE_UNDER_MINIMUM_PRICE);
        }

        if (maximumAmount.isLessThan(MINIMUM_AMOUNT)) {
            throw new FundingException(MAXIMUM_AMOUNT_LESS_THAN_MINIMUM);
        }

        if (product.getPrice().isLessThan(maximumAmount)) {
            throw new FundingException(PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT);
        }

        if (endDate.isBefore(LocalDate.now())) {
            throw new FundingException(INVALID_END_DATE);
        }
    }

    public void participate(Member member, Price amount, String message) {
        if (this.member.equals(member)) {
            throw new FundingException(OWNER_CANNOT_PARTICIPATE);
        }

        if (possibleMaxAmount().isLessThan(amount)) {
            throw new FundingException(EXCEEDED_POSSIBLE_AMOUNT);
        }

        if (amount.isLessThan(minimumAmount) && !amount.equals(remainAmount())) {
            throw new FundingException(UNDER_MINIMUM_AMOUNT);
        }
        FundingParticipant fundingParticipant = new FundingParticipant(member, this, amount, message);
        participants.add(fundingParticipant);
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

    public int getFundingRate() {
        Price fundedAmount = getFundedAmount();
        return (int) (fundedAmount.divide(product.getPrice())
                .value()
                .doubleValue()
                * 100);
    }
}

package moa.funding.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.funding.domain.Price.ZERO;
import static moa.funding.exception.FundingExceptionType.INVALID_END_DATE;
import static moa.funding.exception.FundingExceptionType.MAXIMUM_AMOUNT_GREATER_THAN_PRODUCT;
import static moa.funding.exception.FundingExceptionType.MAXIMUM_AMOUNT_LESS_THAN_MINIMUM;

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

    @Column(name = "visible")
    @Enumerated(STRING)
    private Visibility visible;

    @Column(name = "status")
    @Enumerated(STRING)
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

    @OneToMany(fetch = LAZY)
    @JoinColumn(name = "funding_id")
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
        if (maximumAmount.isLessThan(MINIMUM_AMOUNT)) {
            throw new FundingException(MAXIMUM_AMOUNT_LESS_THAN_MINIMUM);
        }

        if (maximumAmount.isGreaterThan(product.getPrice())) {
            throw new FundingException(MAXIMUM_AMOUNT_GREATER_THAN_PRODUCT);
        }

        if (endDate.isBefore(LocalDate.now())) {
            throw new FundingException(INVALID_END_DATE);
        }
    }

    public Price getFundedAmount() {
        return participants.stream()
                .map(FundingParticipant::getAmount)
                .reduce(ZERO, Price::add);
    }

    public Double getFundingRate() {
        Price fundedAmount = participants.stream()
                .map(FundingParticipant::getAmount)
                .reduce(ZERO, Price::add);
        return fundedAmount.divide(maximumAmount).value().doubleValue() * 100;
    }
}

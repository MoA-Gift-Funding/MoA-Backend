package moa.funding.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static moa.funding.exception.FundingExceptionType.INVALID_END_DATE;
import static moa.funding.exception.FundingExceptionType.INVALID_FUNDING_STATUS;
import static moa.funding.exception.FundingExceptionType.INVALID_MINIMUM_PRICE;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.funding.exception.FundingException;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;
import moa.product.domain.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Funding extends RootEntity<Long> {

    public static final Price MINIMUM_PRICE = new Price("5000");

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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "maximum_price"))
    private Price maximumPrice;

    @Embedded
    private Address deliveryAddress;

    @Column(name = "visible")
    @Enumerated(STRING)
    private Visibility visible;

    @Column(name = "status")
    @Enumerated(STRING)
    private FundingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public Funding(String title, String description, LocalDate endDate, Price maximumPrice,
                    Address deliveryAddress, Visibility visible, FundingStatus status,
                   Member member, Product product) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.maximumPrice = maximumPrice;
        this.deliveryAddress = deliveryAddress;
        this.visible = visible;
        this.status = status;
        this.member = member;
        this.product = product;
    }

    public void create() {
        if (!maximumPrice.isZero() && (maximumPrice.isLessThan(MINIMUM_PRICE))) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }

        if (endDate.isBefore(LocalDate.now())) {
            throw new FundingException(INVALID_END_DATE);
        }

        if (status != FundingStatus.PREPARING) {
            throw new FundingException(INVALID_FUNDING_STATUS);
        }
    }

}

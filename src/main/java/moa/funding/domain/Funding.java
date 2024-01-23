package moa.funding.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static moa.funding.exception.FundingExceptionType.INVALID_MINIMUM_PRICE;

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
import java.math.BigDecimal;
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

    public static final BigDecimal MINIMUM_PRICE = new BigDecimal("5000");

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

    @Column(name = "maximum_price")
    private BigDecimal maximumPrice;

    @Column(name = "minimum_price")
    private BigDecimal minimumPrice;

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

    public Funding(String title, String description, LocalDate endDate, BigDecimal maximumPrice,
                   BigDecimal minimumPrice, Address deliveryAddress, Visibility visible, FundingStatus status,
                   Member member, Product product) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.deliveryAddress = deliveryAddress;
        this.visible = visible;
        this.status = status;
        this.member = member;
        this.product = product;
    }

    public void create() {
        // 최소 펀딩 금액이 기준과 다르면 예외
        if (minimumPrice.compareTo(MINIMUM_PRICE) != 0) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }

        // 최대 펀딩 금액이 0이면 무제한이므로 검증하지 않음
        if (maximumPrice.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // 최대 펀딩 금액이 최소 펀딩 금액보다 작으면 예외
        if (minimumPrice.compareTo(maximumPrice) > 0) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }

        // 펀딩 종료일이 현재 날짜보다 이전이면 예외
        if (endDate.isBefore(LocalDate.now())) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }

        // 펀딩 상태가 기준과 다르면 예외
        if (status != FundingStatus.PREPARING) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }
    }

}

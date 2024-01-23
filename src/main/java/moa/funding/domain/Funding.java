package moa.funding.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import static jakarta.persistence.EnumType.STRING;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Funding extends RootEntity<Long> {

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

    // TODO: Product와 연관관계
    //  @ManyToOne(fetch = FetchType.LAZY)
    //  @JoinColumn(name = "product_id")
    //  private Product product;

    public Funding(String title, String description, LocalDate endDate, BigDecimal maximumPrice,
                   BigDecimal minimumPrice, Address deliveryAddress, Visibility visible, FundingStatus status,
                   Member member) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.deliveryAddress = deliveryAddress;
        this.visible = visible;
        this.status = status;
        this.member = member;
    }
}

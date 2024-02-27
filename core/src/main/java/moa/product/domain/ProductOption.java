package moa.product.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "UK_product_option_product_code",
                columnNames = {"product_id", "code"}
        )
})
public class ProductOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String optionName;

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(STRING)
    @Column(nullable = false)
    private ProductOptionStatus status;

    public ProductOption(String optionName, String code, Product product) {
        this.optionName = optionName;
        this.code = code;
        this.product = product;
    }
}

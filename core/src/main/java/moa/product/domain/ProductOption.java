package moa.product.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "code"})
})
public class ProductOption {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String optionName;

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public ProductOption(String optionName, String code, Product product) {
        this.optionName = optionName;
        this.code = code;
        this.product = product;
    }
}

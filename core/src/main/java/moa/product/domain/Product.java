package moa.product.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.Price;
import moa.global.domain.RootEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "product_id_unique",
                columnNames = {
                        "product_id",
                        "product_provider"
                }
        ),
})
public class Product extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private ProductId productId;

    @Column
    private String imageUrl;

    private String brand;

    private String category;

    private String productName;

    @Embedded
    private Price price;

    @Lob
    @Column(name = "photo", columnDefinition = "BLOB")
    private String description;

    private LocalDate saleEndDate;

    private int discountRate;

    private int limitDate;

    @Enumerated(STRING)
    private ProductStatus status;

    @OneToMany(fetch = LAZY, mappedBy = "product")
    private List<ProductOption> options = new ArrayList<>();

    public Product(
            ProductId productId,
            String imageUrl,
            String brand,
            String category,
            String productName,
            Price price,
            String description,
            LocalDate saleEndDate,
            int discountRate,
            int limitDate
    ) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.category = category;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.saleEndDate = saleEndDate;
        this.discountRate = discountRate;
        this.limitDate = limitDate;
    }
}

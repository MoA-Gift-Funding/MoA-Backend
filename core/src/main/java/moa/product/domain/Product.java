package moa.product.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class Product extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ProductId productId;

    @Column
    private String imageUrl;

    private String brand;

    private String category;

    private String name;

    @Embedded
    private Price price;

    private String description;

    private LocalDate saleEndDate;

    private int discountRate;

    private int limitDate;

    @OneToMany(fetch = LAZY, mappedBy = "product")
    private List<ProductOption> options = new ArrayList<>();

    public Product(
            ProductId productId,
            String imageUrl,
            String brand,
            String category,
            String name,
            Price price,
            String description,
            LocalDate saleEndDate,
            int discountRate,
            int limitDate,
            List<ProductOption> options
    ) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
        this.saleEndDate = saleEndDate;
        this.discountRate = discountRate;
        this.limitDate = limitDate;
        this.options = options;
    }
}

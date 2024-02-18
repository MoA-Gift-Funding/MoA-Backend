package moa.product.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
public class Product extends RootEntity<ProductId> {

    @EmbeddedId
    private ProductId id;

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
            ProductId id,
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
        this.id = id;
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

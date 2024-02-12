package moa.product.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.Price;
import moa.global.domain.RootEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Product extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private String name;

    @Embedded
    private Price price;

    @Column
    private String imageUrl;

    public Product(String name, Price price) {
        this.name = name;
        this.price = price;
    }
}

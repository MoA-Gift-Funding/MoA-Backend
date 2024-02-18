package moa.product.domain;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class ProductId {

    @Column(nullable = false, name = "product_id")
    private String productId;  // 각 상품 제공자에서 상품을 구별하기 위해 사용되는 아이디

    @Enumerated(STRING)
    @Column(nullable = false, name = "product_provider")
    private ProductProvider productProvider;

    public ProductId(String productId, ProductProvider productProvider) {
        this.productId = productId;
        this.productProvider = productProvider;
    }

    public enum ProductProvider {
        WINCUBE,
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductId productId1)) {
            return false;
        }
        return Objects.equals(getProductId(), productId1.getProductId())
               && getProductProvider() == productId1.getProductProvider();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getProductProvider());
    }
}

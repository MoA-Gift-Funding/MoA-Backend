package moa.product.domain;

import static moa.product.exception.ProductExceptionType.NOT_FOUND_PRODUCT;

import java.util.Optional;
import moa.product.exception.ProductException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
    }

    default Product getByProductId(ProductId productId) {
        return findByProductId(productId)
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
    }

    Optional<Product> findByProductId(ProductId productId);
}

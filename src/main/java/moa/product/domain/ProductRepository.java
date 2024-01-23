package moa.product.domain;

import moa.product.exception.ProductException;
import moa.product.exception.ProductExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT));
    }
}

package moa.product.domain;

import static moa.product.exception.ProductExceptionType.NOT_FOUND_PRODUCT;

import moa.product.exception.ProductException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
    }
}

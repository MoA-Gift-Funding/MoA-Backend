package moa.product.query;

import static moa.product.exception.ProductExceptionType.NOT_FOUND_PRODUCT;

import java.time.LocalDate;
import moa.product.domain.Product;
import moa.product.exception.ProductException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductQueryRepository extends JpaRepository<Product, Long> {

    default Product getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
    }

    @Query("""
            SELECT p FROM Product p
            WHERE p.status = 'SALES'
            AND p.saleEndDate >= :afterFiveWeek
            """)
    Page<Product> findAllOnSale(
            @Param("afterFiveWeek") LocalDate afterFiveWeek,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Product p
            WHERE p.status = 'SALES'
            AND p.category = :category
            AND p.saleEndDate >= :afterFiveWeek
            """)
    Page<Product> findAllOnSaleByCategory(
            @Param("category") String category,
            @Param("afterFiveWeek") LocalDate afterFiveWeek,
            Pageable pageable
    );
}

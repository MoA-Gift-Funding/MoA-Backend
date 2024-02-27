package moa.product.query;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import moa.product.query.response.ProductDetailResponse;
import moa.product.query.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductQueryRepository productQueryRepository;

    public Page<ProductResponse> findAllOnSale(Pageable pageable) {
        LocalDate afterFiveWeek = LocalDate.now().plusWeeks(5);
        return productQueryRepository.findAllOnSale(afterFiveWeek, pageable)
                .map(ProductResponse::from);
    }

    public Page<ProductResponse> findAllOnSaleByCategory(String category, Pageable pageable) {
        LocalDate afterFiveWeek = LocalDate.now().plusWeeks(5);
        return productQueryRepository.findAllOnSaleByCategory(category, afterFiveWeek, pageable)
                .map(ProductResponse::from);
    }

    public ProductDetailResponse findById(Long productId) {
        return ProductDetailResponse.from(productQueryRepository.getById(productId));
    }
}

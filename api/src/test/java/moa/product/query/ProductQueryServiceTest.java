package moa.product.query;

import static moa.product.domain.ProductStatus.SALES;
import static moa.product.domain.ProductStatus.SALES_DISCONTINUED;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import moa.ApplicationTest;
import moa.fixture.ProductFixture;
import moa.global.domain.Price;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import moa.product.domain.ProductStatus;
import moa.product.query.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("상품 조회 서비스 (ProductQueryService) 은(는)")
class ProductQueryServiceTest {

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 상품_목록을_조회하는데_판매_중지된_상품_혹은_판매_종료_5주전_상품은_보이지_않는다() {
        // given
        Pageable unpaged = Pageable.unpaged();
        LocalDate today = LocalDate.now();
        productSave("빼뺴로", SALES, today.plusWeeks(4));
        productSave("에어팟", SALES, today.plusWeeks(5));
        productSave("맥북", SALES, today.plusWeeks(5).plusDays(1));
        productSave("말랑", SALES_DISCONTINUED, today.plusYears(1));

        // when
        Page<ProductResponse> result = productQueryService.findAllOnSale(unpaged);

        // then
        List<ProductResponse> content = result.getContent();
        assertThat(content)
                .hasSize(2)
                .extracting(ProductResponse::productName)
                .containsExactlyInAnyOrder("에어팟", "맥북");
    }

    public Product productSave(String name, ProductStatus status, LocalDate saleEndDate) {
        Product product = ProductFixture.product(name, Price.from("10000"));
        ReflectionTestUtils.setField(product, "status", status);
        ReflectionTestUtils.setField(product, "saleEndDate", saleEndDate);
        productRepository.save(product);
        return product;
    }
}

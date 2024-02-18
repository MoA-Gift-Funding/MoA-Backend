package moa.product;

import static moa.product.client.dto.WincubeProductResponse.SUCCESS_CODE;
import static moa.product.domain.ProductId.ProductProvider.WINCUBE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import moa.BatchTest;
import moa.global.domain.Price;
import moa.product.client.WincubeClient;
import moa.product.client.dto.WincubeProductResponse;
import moa.product.client.dto.WincubeProductResponse.Result;
import moa.product.client.dto.WincubeProductResponse.Value;
import moa.product.domain.Product;
import moa.product.domain.ProductId;
import moa.product.domain.ProductOption;
import moa.product.domain.ProductOptionRepository;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

@BatchTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class WincubeProductUpdateJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job wincubeProductUpdateJob;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private WincubeClient wincubeClient;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(wincubeProductUpdateJob);
    }

    @Test
    void 윈큐브로부터_상품정보를_받아와서_새로운_상품이면_추가하고_기존_상품이면_업데이트한_뒤_기존에는_존재했으나_사라진_데이터는_제거한다() throws Exception {
        // given
        Product product1 = productRepository.save(product("1"));
        Product product2 = productRepository.save(product("2"));
        productOptionRepository.saveAll(List.of(
                productOption("초코맛", "1", product1),
                productOption("말랑맛", "2", product1)
        ));
        given(wincubeClient.getProductList())
                .willReturn(new WincubeProductResponse(
                        new Result(SUCCESS_CODE, "2"),
                        new Value(List.of(
                                new HashMap<>() {{
                                    put("goods_id", "1");
                                    put("affiliate", "GS25");
                                    put("affiliate_category", "편의점");
                                    put("desc", "왕 맛난 빼빼로");
                                    put("goods_nm", "빼빼로");
                                    put("goods_img", "http://빼빼로.jpg");
                                    put("normal_sale_price", "1000");
                                    put("period_end", "20291231");
                                    put("limit_date", "60");
                                    put("opt1_name", "초코맛");
                                    put("opt1_val", "1");
                                    put("opt2_name", "딸기맛");
                                    put("opt2_val", "3");
                                    put("opt3_name", "마라탕맛");
                                    put("opt3_val", "4");
                                }},
                                new HashMap<>() {{
                                    put("goods_id", "3");
                                    put("affiliate", "CU");
                                    put("affiliate_category", "편의점");
                                    put("desc", "안성의 자랑 안성탕면");
                                    put("goods_nm", "안성탕면");
                                    put("goods_img", "http://안성탕면.jpg");
                                    put("normal_sale_price", "1200");
                                    put("period_end", "20291211");
                                    put("limit_date", "70");
                                }}
                        ))
                ));
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        transactionTemplate.executeWithoutResult(status -> {
            List<Product> all = productRepository.findAll();
            Optional<Product> updated = productRepository.findByProductId(new ProductId("1", WINCUBE));
            Optional<Product> deleted = productRepository.findByProductId(new ProductId("2", WINCUBE));
            Optional<Product> inserted = productRepository.findByProductId(new ProductId("3", WINCUBE));
            assertThat(updated).isPresent();
            assertThat(deleted).isEmpty();
            assertThat(inserted).isPresent();
            assertThat(updated.get().getProductName()).isEqualTo("빼빼로");
            assertThat(updated.get().getOptions())
                    .extracting(ProductOption::getOptionName)
                    .containsExactlyInAnyOrder("초코맛", "딸기맛", "마라탕맛");
            assertThat(inserted.get().getProductName()).isEqualTo("안성탕면");
        });
    }

    @Test
    void API_에서_문제가_발생하면_예외() throws Exception {
        // given
        given(wincubeClient.getProductList())
                .willReturn(new WincubeProductResponse(
                        new Result("9999", "0"),
                        new Value(List.of())
                ));
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
        assertThat(jobExecution.getExitStatus().getExitDescription()).contains("Wincube 상품 조회 API 실패 ");
    }

    private Product product(String id) {
        return new Product(
                new ProductId(id, WINCUBE),
                "image",
                "brand",
                "category",
                "상품" + id,
                Price.from("10000"),
                "description",
                LocalDate.now().plusDays(1),
                0,
                60
        );
    }

    private ProductOption productOption(String name, String code, Product product) {
        ProductOption productOption = new ProductOption(name, code, product);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(productOption, "createdDate", now);
        ReflectionTestUtils.setField(productOption, "updatedDate", now);
        return productOption;
    }
}

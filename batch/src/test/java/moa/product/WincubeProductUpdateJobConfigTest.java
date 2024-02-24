package moa.product;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.funding.domain.FundingStatus.CANCELLED;
import static moa.funding.domain.FundingStatus.COMPLETE;
import static moa.funding.domain.FundingStatus.EXPIRED;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.funding.domain.FundingStatus.STOPPED;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.product.client.dto.WincubeProductResponse.SUCCESS_CODE;
import static moa.product.domain.ProductId.ProductProvider.WINCUBE;
import static moa.product.domain.ProductOptionStatus.NOT_SUPPORTED;
import static moa.product.domain.ProductStatus.SALES;
import static moa.product.domain.ProductStatus.SALES_DISCONTINUED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import moa.BatchTest;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationRepository;
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
    private MemberRepository memberRepository;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private WincubeClient wincubeClient;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(wincubeProductUpdateJob);
    }

    @Test
    void 윈큐브로부터_상품정보를_받아와서_새로운_상품이면_추가하고_기존_상품이면_업데이트_후_기존에는_존재했으나_사라진_상품의_상태는_판매중지로_변경() throws Exception {
        // given
        Product product1 = productRepository.save(product("1"));
        Product product2 = productRepository.save(product("2"));
        productOptionRepository.saveAll(List.of(
                productOption("초코맛", "1", product1),
                productOption("말랑맛", "2", product1),
                productOption("키키키", "1", product2)
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
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        transactionTemplate.executeWithoutResult(status -> {
            List<Product> all = productRepository.findAll();
            assertThat(all).hasSize(3);
            Product updated = productRepository.getByProductId(new ProductId("1", WINCUBE));
            Product deleted = productRepository.getByProductId(new ProductId("2", WINCUBE));
            Product inserted = productRepository.getByProductId(new ProductId("3", WINCUBE));

            assertThat(updated.getProductName()).isEqualTo("빼빼로");
            assertThat(updated.getStatus()).isEqualTo(SALES);
            assertThat(updated.getOptions())
                    .extracting(ProductOption::getOptionName)
                    .containsExactlyInAnyOrder("초코맛", "말랑맛", "딸기맛", "마라탕맛");
            List<String> notSupportedOptions = updated.getOptions().stream()
                    .filter(it -> it.getStatus().equals(NOT_SUPPORTED))
                    .map(ProductOption::getOptionName)
                    .toList();
            assertThat(notSupportedOptions).containsOnly("말랑맛");

            assertThat(deleted.getStatus()).isEqualTo(SALES_DISCONTINUED);
            assertThat(deleted.getOptions())
                    .hasSize(1)
                    .extracting(ProductOption::getStatus)
                    .containsOnly(NOT_SUPPORTED);

            assertThat(inserted.getProductName()).isEqualTo("안성탕면");
        });
    }

    @Test
    void 진행중인_펀딩_중_상품이_판매중지된_펀딩이_있으면_즉시_중단하고_알림을_전송한다() throws Exception {
        Product product = productRepository.save(product("1"));
        Member member = memberRepository.save(member(null, "mal", "010-1111-1111", SIGNED_UP));
        Funding processing = fundingRepository.save(funding(member, product, PROCESSING));
        Funding expired = fundingRepository.save(funding(member, product, EXPIRED));
        Funding canceled = fundingRepository.save(funding(member, product, CANCELLED));
        Funding completed = fundingRepository.save(funding(member, product, COMPLETE));
        given(wincubeClient.getProductList())
                .willReturn(new WincubeProductResponse(
                        new Result(SUCCESS_CODE, "2"),
                        new Value(List.of(
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
        transactionTemplate.executeWithoutResult(status -> {
            Funding stopped = fundingRepository.getById(processing.getId());
            assertThat(stopped.getStatus()).isEqualTo(STOPPED);

            Funding nonUpdateExpired = fundingRepository.getById(expired.getId());
            assertThat(nonUpdateExpired.getStatus()).isEqualTo(EXPIRED);

            Funding nonUpdateCanceled = fundingRepository.getById(canceled.getId());
            assertThat(nonUpdateCanceled.getStatus()).isEqualTo(CANCELLED);

            Funding nonUpdateCompleted = fundingRepository.getById(completed.getId());
            assertThat(nonUpdateCompleted.getStatus()).isEqualTo(COMPLETE);

            assertThat(notificationRepository.findAll())
                    .hasSize(1)
                    .extracting(Notification::getTitle)
                    .containsOnly("펀딩 중단");
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
        setField(productOption, "createdDate", now);
        setField(productOption, "updatedDate", now);
        return productOption;
    }
}

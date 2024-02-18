package moa.product;

import static moa.product.domain.ProductId.ProductProvider.WINCUBE;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.product.client.WincubeClient;
import moa.product.client.dto.WincubeProductResponse;
import moa.product.client.dto.WincubeProductResponse.Value.WincubeGoods;
import moa.product.client.dto.WincubeProductResponse.Value.WincubeGoods.Option;
import moa.product.domain.Product;
import moa.product.domain.ProductId;
import moa.product.domain.ProductRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WincubeProductUpdateJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final WincubeClient wincubeClient;
    private final ProductRepository productRepository;

    /**
     * 윈큐브 API를 호출하여 상품 목록을 받아온다.
     * <p/>
     * 상품 목록을 우리 서비스에서 사용하는 상품 엔티티(Product)로 변환한 뒤 기존 상품을 모두 업데이트한다.
     * <p/>
     * 모든 작업 이후 업데이트 일자가 @param now 가 아닌 상품과 상품 옵션은,
     * <p/>
     * 제거된 데이터라고 판단하여 모두 제거한다
     */
    @Bean
    public Job wincubeProductUpdateJob() {
        return new JobBuilder("wincubeProductUpdateJob", jobRepository)
                .start(wincubeProductUpdateStep(null))
                .next(deleteRemovedProductStep(null))
                .build();
    }

    /**
     * 윈큐브 API를 호출하여 상품 목록을 받아온다.
     * <p/>
     * 상품 목록을 우리 서비스에서 사용하는 상품 엔티티(Product)로 변환한 뒤 기존 상품을 모두 업데이트한다.
     */
    @Bean
    @JobScope
    public Step wincubeProductUpdateStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return new StepBuilder("wincubeProductUpdateStep", jobRepository)
                .<WincubeGoods, ProductDto>chunk(100, transactionManager)
                .reader(wincubeProductReader())
                .processor(wincubeResponseToEntityProcessor(null))
                .writer(productWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<WincubeGoods> wincubeProductReader() {
        log.info("[윈큐브 상품 업데이트 배치] 윈큐브 api 호출");
        WincubeProductResponse response = wincubeClient.getProductList();
        if (!response.isSuccess()) {
            log.error("Wincube 상품 조회 API 실패", response.result());
            throw new RuntimeException("Wincube 상품 조회 API 실패 " + response.result());
        }
        List<WincubeGoods> wincubeGoods = response.value().goodsList();
        return new ListItemReader<>(wincubeGoods);
    }

    @Bean
    @StepScope
    public ItemProcessor<WincubeGoods, ProductDto> wincubeResponseToEntityProcessor(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return goods -> {
            ProductDto product = new ProductDto(goods, now);
            for (Option option : goods.options()) {
                product.addOption(new ProductOptionDto(option, now));
            }
            return product;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<ProductDto> productWriter(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return chunk -> {
            List<? extends ProductDto> items = chunk.getItems();
            namedParameterJdbcTemplate.batchUpdate("""
                    INSERT INTO product 
                    (
                        id,
                        product_id,
                        product_provider,
                        image_url,
                        brand,
                        category,
                        product_name,
                        price,
                        description,
                        sale_end_date,
                        discount_rate,
                        limit_date,
                        created_date,
                        updated_date
                    )
                    VALUES (
                        NULL,
                        :productId,
                        :productProvider,
                        :imageUrl,
                        :brand,
                        :category,
                        :productName,
                        :price,
                        :description,
                        :saleEndDate,
                        :discountRate,
                        :limitDate,
                        :createdDate,
                        :updatedDate
                    )
                    ON DUPLICATE KEY UPDATE
                    image_url = :imageUrl,
                    brand = :brand,
                    category = :category,
                    product_name = :productName,
                    price = :price,
                    description = :description,
                    sale_end_date = :saleEndDate,
                    discount_rate = :discountRate,
                    limit_date = :limitDate,
                    updated_date = :updatedDate
                    """, SqlParameterSourceUtils.createBatch(items));
            for (ProductDto item : items) {
                List<ProductOptionDto> options = item.options();
                Product product = productRepository.getByProductId(
                        new ProductId(item.productId(), WINCUBE)
                );
                List<ProductOptionDto> optionsWithProductId = options.stream()
                        .map(it -> it.productId(product.getId()))
                        .toList();
                namedParameterJdbcTemplate.batchUpdate("""
                        INSERT INTO product_option
                        (
                            id,
                            product_id,
                            code,
                            option_name,
                            created_date,
                            updated_date
                        )
                        VALUES (
                            NULL,
                            :productId,
                            :code,
                            :optionName,
                            :createdDate,
                            :updatedDate
                        )
                        ON DUPLICATE KEY UPDATE
                        option_name = :optionName,
                        updated_date = :updatedDate
                        """, SqlParameterSourceUtils.createBatch(optionsWithProductId));
            }
        };
    }

    /**
     * 모든 작업 이후 업데이트 일자가 @Param(now) 가 아닌 상품과 상품 옵션은,
     * <p/>
     * 제거된 데이터라고 판단하여 모두 제거한다
     */
    @Bean
    @JobScope
    public Step deleteRemovedProductStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        return new StepBuilder("deleteRemovedProductStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<Long> deleteCandidateProductIds = namedParameterJdbcTemplate.query("""
                                    SELECT id
                                    FROM product
                                    WHERE product_provider = 'WINCUBE'
                                    AND updated_date < :now
                                    """,
                            Map.of("now", now),
                            new SingleColumnRowMapper<Long>());

                    namedParameterJdbcTemplate.update("""
                            DELETE FROM product_option po
                            WHERE po.product_id IN (:ids)
                            """, Map.of("ids", deleteCandidateProductIds));

                    namedParameterJdbcTemplate.update("""
                            DELETE FROM product p
                            WHERE p.id IN (:ids)
                            """, Map.of("ids", deleteCandidateProductIds));

                    namedParameterJdbcTemplate.update("""
                            DELETE FROM product_option po
                            WHERE po.id IN (
                                SELECT po.id
                                FROM product_option po
                                JOIN product p ON p.id = po.product_id
                                WHERE po.updated_date < :now
                                AND p.product_provider = 'WINCUBE'
                            ) 
                            """, Map.of("now", now));
                    return FINISHED;
                }, transactionManager)
                .build();
    }
}

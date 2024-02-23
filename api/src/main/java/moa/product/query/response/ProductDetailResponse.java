package moa.product.query.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import moa.product.domain.Product;
import moa.product.domain.ProductExchangeRefundPolicy;
import moa.product.domain.ProductId;
import moa.product.domain.ProductOption;
import moa.product.domain.ProductOptionStatus;
import moa.product.domain.ProductStatus;

public record ProductDetailResponse(
        @Schema(description = "상품 id")
        Long id,

        @Schema(description = "상품 제공업체 + 제공업체의 상품 id")
        ProductId productId,

        @Schema(description = "상품 이미지 url")
        String imageUrl,

        String brand,
        String category,
        String productName,
        long price,
        String description,

        @Schema(description = "판매종료일")
        LocalDate saleEndDate,
        int discountRate,

        @Schema(description = "유효기간")
        int limitDate,

        @Schema(description = "상품의 상태")
        ProductStatus status,

        List<ProductOptionResponse> options,

        @Schema(description = "상품 교환 / 환불 규정")
        ProductExchangeRefundPolicy productExchangeRefundPolicy
) {
    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getProductId(),
                product.getImageUrl(),
                product.getBrand(),
                product.getCategory(),
                product.getProductName(),
                product.getPrice().longValue(),
                product.getDescription(),
                product.getSaleEndDate(),
                product.getDiscountRate(),
                product.getLimitDate(),
                product.getStatus(),
                ProductOptionResponse.from(product.getOptions()),
                new ProductExchangeRefundPolicy()
        );
    }

    public record ProductOptionResponse(
            @Schema(description = "상품 옵션 id")
            Long id,

            @Schema(description = "상품 옵션 이름")
            String optionName,

            @Schema(description = "상품 옵션 코드")
            String code,

            @Schema(description = "옵션의 지원여부 상태")
            ProductOptionStatus status
    ) {
        public static List<ProductOptionResponse> from(List<ProductOption> options) {
            return options.stream()
                    .map(it -> new ProductOptionResponse(
                            it.getId(),
                            it.getOptionName(),
                            it.getCode(),
                            it.getStatus()
                    ))
                    .toList();
        }
    }
}

package moa.product.query.response;

import io.swagger.v3.oas.annotations.media.Schema;
import moa.product.domain.Product;
import moa.product.domain.ProductId;
import moa.product.domain.ProductStatus;

public record ProductResponse(
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
        int discountRate,

        @Schema(description = "유효기간")
        int limitDate,

        @Schema(description = "상품의 상태")
        ProductStatus status
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductId(),
                product.getImageUrl(),
                product.getBrand(),
                product.getCategory(),
                product.getProductName(),
                product.getPrice().longValue(),
                product.getDiscountRate(),
                product.getLimitDate(),
                product.getStatus()
        );
    }
}

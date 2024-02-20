package moa.product;

import java.time.LocalDateTime;
import moa.product.client.dto.WincubeProductResponse.Value.WincubeGoods.Option;

public record ProductOptionDto(
        String optionName,
        String code,
        Long productId,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    public ProductOptionDto(Option option, LocalDateTime now) {
        this(
                option.name(),
                option.code(),
                null,
                now,
                now
        );
    }

    public ProductOptionDto productId(Long id) {
        return new ProductOptionDto(
                optionName,
                code,
                id,
                createdDate,
                updatedDate
        );
    }
}

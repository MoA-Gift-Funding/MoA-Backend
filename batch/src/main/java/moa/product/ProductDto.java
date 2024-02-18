package moa.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import moa.product.client.dto.WincubeProductResponse.Value.WincubeGoods;
import moa.product.domain.ProductId.ProductProvider;

public record ProductDto(
        String productId,
        String productProvider,
        String imageUrl,
        String brand,
        String category,
        String productName,
        BigDecimal price,
        String description,
        LocalDate saleEndDate,
        int discountRate,
        int limitDate,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,
        List<ProductOptionDto> options
) {
    public ProductDto(WincubeGoods goods, LocalDateTime now) {
        this(
                goods.goodsId(),
                ProductProvider.WINCUBE.name(),
                goods.goodsImg(),
                goods.affiliate(),
                goods.affiliateCategory(),
                goods.goodsNm(),
                new BigDecimal(goods.normalSalePrice()),
                goods.desc(),
                goods.periodEnd(),
                0,
                goods.limitDate(),
                now,
                now,
                new ArrayList<>()
        );
    }

    public void addOption(ProductOptionDto option) {
        options.add(option);
    }
}

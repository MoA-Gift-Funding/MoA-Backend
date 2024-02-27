package moa.order.query.response;

import java.time.LocalDateTime;
import moa.order.domain.Order;
import moa.order.domain.OrderStatus;
import moa.product.domain.Product;
import moa.product.domain.ProductId;

public record OrderResponse(
        Long orderId,
        ProductId productId,
        String imageUrl,
        String brand,
        String category,
        String productName,
        Long price,
        OrderStatus status,
        LocalDateTime orderDate
) {
    public static OrderResponse from(Order order) {
        Product product = order.getProduct();
        return new OrderResponse(
                order.getId(),
                product.getProductId(),
                product.getImageUrl(),
                product.getBrand(),
                product.getCategory(),
                product.getProductName(),
                product.getPrice().longValue(),
                order.getStatus(),
                order.getCreatedDate()
        );
    }
}

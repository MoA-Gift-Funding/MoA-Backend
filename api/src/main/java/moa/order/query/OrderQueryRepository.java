package moa.order.query;


import static moa.order.exception.OrderExceptionType.NOT_FOUND_ORDER;

import java.util.Optional;
import moa.order.domain.Order;
import moa.order.exception.OrderException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderQueryRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByMemberId(Long memberId, Pageable pageable);

    default Order getWithFundingAndProductById(Long orderId) {
        return findWithFundingAndProductById(orderId)
                .orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));
    }

    @EntityGraph(attributePaths = {"funding", "product"})
    Optional<Order> findWithFundingAndProductById(Long orderId);
}

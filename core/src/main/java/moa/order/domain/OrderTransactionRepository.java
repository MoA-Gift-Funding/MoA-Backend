package moa.order.domain;

import static moa.order.exception.OrderExceptionType.NOT_FOUND_ORDER_TX;

import java.util.Optional;
import moa.order.exception.OrderException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

    default OrderTransaction getLastedByOrder(Order order) {
        return findFirstByOrderOrderByCreatedDateDesc(order)
                .orElseThrow(() -> new OrderException(NOT_FOUND_ORDER_TX));
    }

    Optional<OrderTransaction> findFirstByOrderOrderByCreatedDateDesc(Order order);
}

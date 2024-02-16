package moa.order.domain;

import static moa.order.exception.OrderExceptionType.NOT_FOUND_ORDER;

import moa.order.exception.OrderException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    default Order getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));
    }
}

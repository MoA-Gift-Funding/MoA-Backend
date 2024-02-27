package moa.order.domain;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import static moa.order.exception.OrderExceptionType.NOT_FOUND_ORDER;

import java.util.Optional;
import moa.order.exception.OrderException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface OrderRepository extends JpaRepository<Order, Long> {

    default Order getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));
    }

    default Order getWithLockById(Long id) {
        return findWithLockById(id)
                .orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));
    }

    @Lock(PESSIMISTIC_WRITE)
    Optional<Order> findWithLockById(Long id);
}

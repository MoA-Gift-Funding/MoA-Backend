package moa.delivery.domain;

import static moa.delivery.exception.DeliveryExceptionType.NOT_FOUND_DELIVERY;

import moa.delivery.exception.DeliveryException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    default Delivery getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new DeliveryException(NOT_FOUND_DELIVERY));
    }
}

package moa.delivery.domain;

import moa.delivery.exception.DeliveryException;
import moa.delivery.exception.DeliveryExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    default Delivery getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new DeliveryException(DeliveryExceptionType.NOT_FOUND_DELIVERY));
    }
}

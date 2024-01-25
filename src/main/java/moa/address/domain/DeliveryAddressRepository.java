package moa.address.domain;

import static moa.address.exception.DeliveryAddressExceptionType.NOT_FOUND_ADDRESS;

import moa.address.exception.DeliveryAddressException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    default DeliveryAddress getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new DeliveryAddressException(NOT_FOUND_ADDRESS));
    }
}

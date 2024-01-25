package moa.address.domain;

import static moa.address.exception.DeliveryAddressExceptionType.NOT_FOUND_ADDRESS;

import java.util.List;
import moa.address.exception.DeliveryAddressException;
import moa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    default DeliveryAddress getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new DeliveryAddressException(NOT_FOUND_ADDRESS));
    }

    default AddressBook getAddressBookByMember(Member member) {
        return new AddressBook(findByMember(member));
    }

    List<DeliveryAddress> findByMember(Member member);
}

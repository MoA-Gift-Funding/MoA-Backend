package moa.address.query;

import java.util.List;
import moa.address.domain.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressQueryRepository extends JpaRepository<DeliveryAddress, Long> {

    List<DeliveryAddress> findByMemberId(Long memberId);
}

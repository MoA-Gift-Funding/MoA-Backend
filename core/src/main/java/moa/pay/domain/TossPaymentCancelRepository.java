package moa.pay.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentCancelRepository extends JpaRepository<TossPaymentCancel, Long> {

    Optional<TossPaymentCancel> findByTossPayment(TossPayment payment);
}

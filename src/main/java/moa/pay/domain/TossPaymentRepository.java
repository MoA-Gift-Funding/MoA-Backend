package moa.pay.domain;

import static moa.pay.exception.TossPaymentExceptionType.NOT_FOUND_PAYMENT;

import java.util.Optional;
import moa.pay.exception.TossPaymentException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentRepository extends JpaRepository<TossPayment, String> {

    default TossPayment getByOrderId(String orderId) {
        return findByOrderId(orderId).orElseThrow(() ->
                new TossPaymentException(NOT_FOUND_PAYMENT));
    }

    Optional<TossPayment> findByOrderId(String orderId);
}

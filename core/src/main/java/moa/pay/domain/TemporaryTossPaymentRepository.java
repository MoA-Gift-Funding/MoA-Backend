package moa.pay.domain;

import static moa.pay.exception.TossPaymentExceptionType.PAYMENT_INVALID;

import moa.pay.exception.TossPaymentException;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryTossPaymentRepository extends CrudRepository<TemporaryTossPayment, String> {

    default TemporaryTossPayment getById(String orderId) {
        return findById(orderId).orElseThrow(() ->
                new TossPaymentException(PAYMENT_INVALID.withDetail("orderId:" + orderId))
        );
    }
}

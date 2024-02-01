package moa.pay.domain;

import static moa.pay.exception.TossPaymentExceptionType.PAYMENT_INVALID;

import moa.pay.exception.TossPaymentException;
import org.springframework.data.repository.CrudRepository;

public interface TossPaymentConfirmRepository extends CrudRepository<TossPaymentConfirm, String> {

    default TossPaymentConfirm getById(String orderId) {
        return findById(orderId).orElseThrow(() ->
                new TossPaymentException(PAYMENT_INVALID.withDetail("orderId:" + orderId))
        );
    }
}

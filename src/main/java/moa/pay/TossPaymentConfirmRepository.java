package moa.pay;

import static moa.pay.TossPaymentExceptionType.PAYMENT_INVALID;

import org.springframework.data.repository.CrudRepository;

public interface TossPaymentConfirmRepository extends CrudRepository<TossPaymentConfirm, String> {

    default TossPaymentConfirm getById(String orderId) {
        return findById(orderId).orElseThrow(() ->
                new TossPaymentException(PAYMENT_INVALID.withDetail("orderId:" + orderId))
        );
    }
}

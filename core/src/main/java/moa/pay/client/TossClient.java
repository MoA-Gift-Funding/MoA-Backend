package moa.pay.client;

import lombok.RequiredArgsConstructor;
import moa.pay.client.dto.TossPaymentCancelRequest;
import moa.pay.client.dto.TossPaymentConfirmRequest;
import moa.pay.client.dto.TossPaymentResponse;
import moa.pay.domain.TossPayment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TossClient {

    private final PaymentProperty paymentProperty;
    private final TossApiClient client;

    public TossPaymentResponse confirmPayment(TossPaymentConfirmRequest request) {
        return client.confirmPayment(paymentProperty.basicAuth(), request);
    }

    public TossPaymentResponse cancelPayment(TossPayment payment) {
        return client.cancelPayment(
                payment.getPaymentKey(),
                paymentProperty.basicAuth(),
                payment.getIdempotencyKeyForCancel(),
                new TossPaymentCancelRequest(payment.getCancel().getReason())
        );
    }
}

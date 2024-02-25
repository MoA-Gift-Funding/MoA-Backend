package moa.client.toss;

import lombok.RequiredArgsConstructor;
import moa.client.toss.dto.TossPaymentCancelRequest;
import moa.client.toss.dto.TossPaymentConfirmRequest;
import moa.client.toss.dto.TossPaymentResponse;
import moa.pay.domain.TossPayment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TossClient {

    private final TossPaymentProperty tossPaymentProperty;
    private final TossApiClient client;

    public TossPaymentResponse confirmPayment(TossPaymentConfirmRequest request) {
        return client.confirmPayment(tossPaymentProperty.basicAuth(), request);
    }

    public TossPaymentResponse cancelPayment(TossPayment payment) {
        return client.cancelPayment(
                payment.getPaymentKey(),
                tossPaymentProperty.basicAuth(),
                payment.getIdempotencyKeyForCancel(),
                new TossPaymentCancelRequest(payment.getCancel().getReason())
        );
    }
}

package moa.pay.application.command;

import moa.client.toss.dto.TossPaymentConfirmRequest;

public record PaymentPermitCommand(
        Long memberId,
        String paymentKey,
        String orderId,
        int amount
) {
    public TossPaymentConfirmRequest toConfirmRequest() {
        return new TossPaymentConfirmRequest(paymentKey, orderId, amount);
    }
}

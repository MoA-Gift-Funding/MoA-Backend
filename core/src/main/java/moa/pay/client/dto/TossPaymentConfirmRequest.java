package moa.pay.client.dto;

public record TossPaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}

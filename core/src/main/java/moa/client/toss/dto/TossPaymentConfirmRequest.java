package moa.client.toss.dto;

public record TossPaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}

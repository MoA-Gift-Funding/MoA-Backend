package moa.pay.client.dto;

public record TossPaymentConfirmRequest(
        String orderId,
        Integer amount
) {
}

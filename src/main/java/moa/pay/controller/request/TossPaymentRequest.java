package moa.pay.controller.request;

public record TossPaymentRequest(
        String orderId,
        Integer amount,
        String paymentKey
) {
}

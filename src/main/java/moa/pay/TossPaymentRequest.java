package moa.pay;

public record TossPaymentRequest(
        String orderId,
        Integer amount,
        String paymentKey
) {
}

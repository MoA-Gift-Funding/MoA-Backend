package moa.pay;

public record PaymentSuccessEvent(
        TossPaymentObject paymentObject
) {
}

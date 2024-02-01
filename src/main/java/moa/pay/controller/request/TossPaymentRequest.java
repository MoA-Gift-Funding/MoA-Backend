package moa.pay.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TossPaymentRequest(
        @NotBlank String orderId,
        @Positive int amount,
        @NotBlank String paymentKey
) {
}

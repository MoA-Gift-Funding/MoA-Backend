package moa.pay.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TossPaymentConfirmRequest(
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @Positive int amount
) {
}

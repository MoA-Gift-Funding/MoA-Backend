package moa.pay.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PrepayRequest(
        @Schema(description = "주문 ID, 6자 이상 64자 이하", example = "sample-12341")
        @NotBlank String orderId,

        @Schema(description = "가격", example = "10000")
        @Positive int amount
) {
}

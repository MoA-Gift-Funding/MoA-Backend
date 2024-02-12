package moa.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record SendPhoneVerificationNumberRequest(
        @Schema(example = "010-1234-5678")
        @Pattern(regexp = "\\d{3}-\\d{3,4}-\\d{4}", message = "핸드폰 번호 형식이 올바르지 않습니다. {validatedValue}")
        String phoneNumber
) {
}

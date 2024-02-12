package moa.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import moa.member.application.command.PhoneVerifyCommand;

public record VerifyPhoneRequest(
        @Schema(example = "123456")
        String verificationNumber
) {
    public PhoneVerifyCommand toCommand(Long memberId) {
        return new PhoneVerifyCommand(
                memberId,
                verificationNumber
        );
    }
}

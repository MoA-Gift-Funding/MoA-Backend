package moa.member.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import moa.member.application.command.VerifyPhoneCommand;

public record VerifyPhoneRequest(
        @Schema(example = "123456") String verificationNumber
) {
    public VerifyPhoneCommand toCommand(Long memberId) {
        return new VerifyPhoneCommand(
                memberId,
                verificationNumber
        );
    }
}

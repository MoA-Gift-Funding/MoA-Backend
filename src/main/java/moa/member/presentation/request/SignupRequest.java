package moa.member.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import moa.member.application.command.SignupCommand;

public record SignupRequest(
        @Schema(example = "test@naver.com")
        @Email String email,

        @Schema(example = "짱구")
        @NotNull String nickname,

        @Schema(example = "2012")
        @NotNull String birthyear,

        @Schema(example = "0104")
        @NotNull String birthday,

        String profileImageUrl
) {
    public SignupCommand toCommand(Long memberId) {
        return new SignupCommand(
                memberId,
                email,
                nickname,
                birthday,
                birthyear,
                profileImageUrl
        );
    }
}

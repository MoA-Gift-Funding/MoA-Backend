package moa.member.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import moa.member.application.command.MemberUpdateCommand;

public record MemberUpdateRequest(
        @Schema(example = "짱구")
        @NotNull String nickname,

        @Schema(example = "2000")
        @NotNull String birthyear,

        @Schema(example = "0104")
        @NotNull String birthday,

        @Schema(example = "https://image.com/imagePath")
        String profileImageUrl
) {
    public MemberUpdateCommand toCommand(Long memberId) {
        return new MemberUpdateCommand(
                memberId,
                nickname,
                birthyear,
                birthday,
                profileImageUrl
        );
    }
}

package moa.member.query.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import moa.member.domain.Member;
import moa.member.domain.MemberStatus;
import moa.member.domain.OauthId.OauthProvider;

public record MemberResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "1")
        String oauthId,

        @Schema(example = "KAKAO")
        OauthProvider oauthProvider,
        @Schema(example = "test@gmail.com")
        @Nullable String email,

        @Schema(example = "짱구")
        String nickname,

        @Schema(example = "0104")
        @Nullable String birthday,

        @Schema(example = "2000")
        @Nullable String birthyear,

        @Schema(example = "https://image.com/imagepath")
        @Nullable String profileImageUrl,

        @Schema(example = "010-1234-5678")
        String phoneNumber,

        @Schema(example = "SIGNED_UP")
        MemberStatus status
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getOauthId().getOauthId(),
                member.getOauthId().getOauthProvider(),
                member.getEmail(),
                member.getNickname(),
                member.getBirthday(),
                member.getBirthyear(),
                member.getProfileImageUrl(),
                member.getPhoneNumber(),
                member.getStatus()
        );
    }
}

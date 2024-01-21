package moa.member.infrastructure.oauth.kakao.response;


import static moa.member.domain.OauthId.OauthProvider.KAKAO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import moa.member.domain.Member;
import moa.member.domain.OauthId;

@JsonNaming(value = SnakeCaseStrategy.class)
public record KakaoMemberResponse(
        Long id,
        boolean hasSignedUp,
        LocalDateTime connectedAt,
        KakaoAccount kakaoAccount
) {

    public Member toMember() {
        return Member.builder()
                .oauthId(new OauthId(String.valueOf(id), KAKAO))
                .email(kakaoAccount.email)
                .nickname(kakaoAccount.profile.nickname)
                .birthyear(kakaoAccount.birthyear)
                .birthday(kakaoAccount.birthday)
                .profileImageUrl(kakaoAccount.profile.profileImageUrl)
                .phoneNumber(formattedPhone(kakaoAccount.phoneNumber))
                .build();
    }

    private String formattedPhone(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.contains(" ")) {
            return phoneNumber;
        }
        return phoneNumber.split(" ")[1];
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record KakaoAccount(
            boolean profileNeedsAgreement,
            boolean profileNicknameNeedsAgreement,
            boolean profileImageNeedsAgreement,
            Profile profile,
            boolean nameNeedsAgreement,
            String name,
            boolean emailNeedsAgreement,
            boolean isEmailValid,
            boolean isEmailVerified,
            String email,
            boolean ageRangeNeedsAgreement,
            String ageRange,
            boolean birthyearNeedsAgreement,
            String birthyear,
            boolean birthdayNeedsAgreement,
            String birthday,
            String birthdayType,
            boolean genderNeedsAgreement,
            String gender,
            boolean phoneNumberNeedsAgreement,
            String phoneNumber,
            boolean ciNeedsAgreement,
            String ci,
            LocalDateTime ciAuthenticatedAt
    ) {
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record Profile(
            String nickname,
            String thumbnailImageUrl,
            String profileImageUrl,
            boolean isDefaultImage
    ) {
    }
}

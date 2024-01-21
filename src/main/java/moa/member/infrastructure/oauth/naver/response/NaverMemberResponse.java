package moa.member.infrastructure.oauth.naver.response;


import static moa.member.domain.OauthId.OauthProvider.NAVER;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import moa.member.domain.Member;
import moa.member.domain.OauthId;

@JsonNaming(value = SnakeCaseStrategy.class)
public record NaverMemberResponse(
        String resultcode,
        String message,
        Response response
) {

    public Member toMember() {
        return Member.builder()
                .oauthId(new OauthId(String.valueOf(response.id), NAVER))
                .email(response.email)
                .nickname(response.nickname)
                .birthyear(response.birthyear)
                .birthday(response.birthday.replace("-", ""))
                .profileImageUrl(response.profileImage)
                .phoneNumber(response.mobile)
                .build();
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record Response(
            String id,
            String nickname,
            String name,
            String email,
            String gender,
            String age,
            String birthday,
            String profileImage,
            String birthyear,
            String mobile
    ) {
    }
}

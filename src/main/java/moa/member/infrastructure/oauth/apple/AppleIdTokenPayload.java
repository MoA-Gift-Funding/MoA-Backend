package moa.member.infrastructure.oauth.apple;

import static moa.member.domain.OauthId.OauthProvider.APPLE;

import moa.member.domain.Member;
import moa.member.domain.OauthId;

public record AppleIdTokenPayload(
        String sub,
        String email
) {

    public Member toMember() {
        return Member.builder()
                .oauthId(new OauthId(sub, APPLE))
                .email(email)
                .build();
    }
}

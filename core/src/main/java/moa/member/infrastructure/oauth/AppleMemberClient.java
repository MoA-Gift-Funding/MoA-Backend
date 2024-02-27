package moa.member.infrastructure.oauth;

import static moa.member.domain.OauthId.OauthProvider.APPLE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.oauth.apple.AppleClient;
import moa.client.oauth.apple.response.AppleIdTokenPayload;
import moa.client.oauth.apple.response.AppleTokenResponse;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleMemberClient implements OauthMemberClient {
    private final AppleClient appleClient;

    @Override
    public OauthProvider supportsProvider() {
        return APPLE;
    }

    @Override
    public Member fetch(String authCode) {
        AppleIdTokenPayload payload = appleClient.getIdTokenPayload(authCode);
        AppleTokenResponse tokenResponse = appleClient.fetchToken(authCode);
        return new Member(
                new OauthId(payload.sub(), APPLE, tokenResponse.refreshToken()),
                payload.email(),
                null,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void withDraw(Member member) {
        appleClient.withdraw(member.getOauthId().getRefreshToken());
    }
}

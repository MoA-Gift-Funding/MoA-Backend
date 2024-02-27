package moa.member.infrastructure.oauth;

import static moa.member.domain.OauthId.OauthProvider.KAKAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.oauth.kakao.KakaoClient;
import moa.client.oauth.kakao.KakaoOauthProperty;
import moa.client.oauth.kakao.response.KakaoMemberResponse;
import moa.member.domain.Member;
import moa.member.domain.OauthId;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMemberClient implements OauthMemberClient {

    private final KakaoClient kakaoClient;
    private final KakaoOauthProperty kakaoOauthProperty;

    @Override
    public OauthProvider supportsProvider() {
        return KAKAO;
    }

    @Override
    public Member fetch(String accessToken) {
        KakaoMemberResponse response = kakaoClient.fetchMember(accessToken);
        return new Member(
                new OauthId(String.valueOf(response.id()), KAKAO),
                response.kakaoAccount().email(),
                response.kakaoAccount().profile().nickname(),
                response.kakaoAccount().birthyear(),
                response.kakaoAccount().birthday(),
                response.kakaoAccount().profile().profileImageUrl(),
                response.kakaoAccount().formattedPhone()
        );
    }

    @Override
    public void withdraw(Member member) {
        kakaoClient.withdrawMember(member.getOauthId().getOauthId(), kakaoOauthProperty.adminKey());
    }
}

package moa.member.infrastructure.oauth.kakao;

import static moa.member.domain.OauthId.OauthProvider.KAKAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.member.domain.Member;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClient;
import moa.member.infrastructure.oauth.kakao.response.KakaoMemberResponse;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoMemberClient implements OauthMemberClient {

    private final KakaoApiClient kakaoApiClient;

    @Override
    public OauthProvider supportsProvider() {
        return KAKAO;
    }

    @Override
    public Member fetch(String accessToken) {
        KakaoMemberResponse kakaoMemberResponse = kakaoApiClient.fetchMember("Bearer " + accessToken);
        log.info("카카오톡 로그인 성공: {}", kakaoMemberResponse);
        return kakaoMemberResponse.toMember();
    }
}

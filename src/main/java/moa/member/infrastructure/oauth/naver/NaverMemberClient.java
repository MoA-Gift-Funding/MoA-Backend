package moa.member.infrastructure.oauth.naver;

import static moa.member.domain.OauthId.OauthProvider.NAVER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.member.domain.Member;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClient;
import moa.member.infrastructure.oauth.naver.response.NaverMemberResponse;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NaverMemberClient implements OauthMemberClient {

    private final NaverApiClient naverApiClient;

    @Override
    public OauthProvider supportsProvider() {
        return NAVER;
    }

    @Override
    public Member fetch(String accessToken) {
        NaverMemberResponse naverMemberResponse = naverApiClient.fetchMember("Bearer " + accessToken);
        log.info("네이버 로그인 성공: {}", naverMemberResponse);
        return naverMemberResponse.toMember();
    }
}

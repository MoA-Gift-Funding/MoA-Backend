package moa.client.oauth.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.oauth.naver.response.NaverMemberResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverClient {

    private final NaverApiClient naverApiClient;
    private final NaverOauthProperty naverOauthProperty;

    public NaverMemberResponse fetchMember(String accessToken) {
        NaverMemberResponse naverMemberResponse = naverApiClient.fetchMember("Bearer " + accessToken);
        log.info("네이버 회원 정보 조회 성공: {}", naverMemberResponse);
        return naverMemberResponse;
    }

    /**
     * https://developers.naver.com/docs/login/devguide/devguide.md#5-3-%EB%84%A4%EC%9D%B4%EB%B2%84-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%97%B0%EB%8F%99-%ED%95%B4%EC%A0%9C
     * 따라서 연동 해제를 수행하기 전에 접근토큰의 유효성을 점검하고 5.1의 접근토큰 갱신 과정에 따라 접근토큰을 갱신하는것을 권장합니다.
     */
    public void withdrawMember(String accessToken) {
        naverApiClient.withdrawMember(
                naverOauthProperty.clientId(),
                naverOauthProperty.clientSecret(),
                accessToken,
                "delete"
        );
        log.info("네이버 회원 탈퇴 성공");
    }
}

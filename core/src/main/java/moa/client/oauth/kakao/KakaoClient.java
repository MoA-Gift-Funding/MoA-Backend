package moa.client.oauth.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.oauth.kakao.response.KakaoMemberResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final KakaoApiClient kakaoApiClient;

    public KakaoMemberResponse fetchMember(String accessToken) {
        KakaoMemberResponse kakaoMemberResponse = kakaoApiClient.fetchMember("Bearer " + accessToken);
        log.info("카카오톡 회원 정보 조회 성공: {}", kakaoMemberResponse);
        return kakaoMemberResponse;
    }

    public void withdrawMember(String accessToken) {
        var result = kakaoApiClient.withdrawMember("Bearer " + accessToken);
        log.info("카카오톡 회원 탈퇴 성공: {}", result);
    }
}

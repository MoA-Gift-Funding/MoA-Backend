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

    public NaverMemberResponse fetchMember(String accessToken) {
        NaverMemberResponse naverMemberResponse = naverApiClient.fetchMember("Bearer " + accessToken);
        log.info("네이버 회원 정보 조회 성공: {}", naverMemberResponse);
        return naverMemberResponse;
    }
}

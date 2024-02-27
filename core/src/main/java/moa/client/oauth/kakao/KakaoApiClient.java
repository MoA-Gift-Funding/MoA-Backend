package moa.client.oauth.kakao;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import moa.client.oauth.kakao.response.KakaoMemberResponse;
import moa.client.oauth.kakao.response.KakaoWithdrawResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
 */
public interface KakaoApiClient {

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);

    @PostExchange(url = "https://kapi.kakao.com/v1/user/unlink")
    KakaoWithdrawResponse withdrawMember(@RequestHeader(name = AUTHORIZATION) String adminToken);
}

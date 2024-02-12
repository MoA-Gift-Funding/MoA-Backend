package moa.member.infrastructure.oauth.kakao;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import moa.member.infrastructure.oauth.kakao.response.KakaoMemberResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

public interface KakaoApiClient {

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}

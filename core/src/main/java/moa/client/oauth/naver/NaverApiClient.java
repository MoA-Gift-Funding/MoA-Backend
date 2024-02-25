package moa.client.oauth.naver;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import moa.client.oauth.naver.response.NaverMemberResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

public interface NaverApiClient {

    @GetExchange("https://openapi.naver.com/v1/nid/me")
    NaverMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}

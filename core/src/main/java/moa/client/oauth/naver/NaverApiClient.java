package moa.client.oauth.naver;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import moa.client.oauth.naver.response.NaverMemberResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface NaverApiClient {

    @GetExchange("https://openapi.naver.com/v1/nid/me")
    NaverMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
    
    @PostExchange("https://nid.naver.com/oauth2.0/token")
    void withdrawMember(
            @RequestHeader(name = "client_id") String clientId,
            @RequestHeader(name = "client_secret") String clientSecret,
            @RequestHeader(name = "access_token") String accessToken,
            @RequestHeader(name = "grant_type", value = "delete") String grantType
    );
}

package moa.client.oauth.naver;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import moa.client.oauth.naver.response.NaverMemberResponse;
import moa.client.oauth.naver.response.NaverTokenResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface NaverApiClient {

    @GetExchange("https://openapi.naver.com/v1/nid/me")
    NaverMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);

    @PostExchange("https://nid.naver.com/oauth2.0/token")
    void withdrawMember(
            @RequestParam(name = "client_id") String clientId,
            @RequestParam(name = "client_secret") String clientSecret,
            @RequestParam(name = "access_token") String accessToken,
            @RequestParam(name = "grant_type", value = "delete") String grantType
    );

    @GetExchange("https://nid.naver.com/oauth2.0/token")
    NaverTokenResponse reFetchToken(
            @RequestParam(name = "client_id") String clientId,
            @RequestParam(name = "client_secret") String clientSecret,
            @RequestParam(name = "access_token") String accessToken,
            @RequestParam(name = "grant_type", value = "refresh_token") String grantType
    );
}

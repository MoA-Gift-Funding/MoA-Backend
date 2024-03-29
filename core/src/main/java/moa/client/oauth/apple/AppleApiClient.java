package moa.client.oauth.apple;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import moa.client.oauth.apple.response.AppleTokenResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface AppleApiClient {

    /**
     * https://developer.apple.com/documentation/accountorganizationaldatasharing/generate-and-validate-tokens
     */
    @PostExchange(url = "https://appleid.apple.com/auth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    AppleTokenResponse fetchToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("grant_type") String grantType
    );

    /**
     * https://developer.apple.com/documentation/sign_in_with_apple/revoke_tokens
     */
    @PostExchange(url = "https://appleid.apple.com/auth/revoke", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    void withdraw(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("token") String token,
            @RequestParam("token_type_hint") String tokenTypeHint
    );
}

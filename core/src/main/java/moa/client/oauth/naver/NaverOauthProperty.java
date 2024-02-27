package moa.client.oauth.naver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth.naver")
public record NaverOauthProperty(
        String clientId,
        String clientSecret
) {
}

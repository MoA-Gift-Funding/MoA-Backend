package moa.client.oauth.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth.kakao")
public record KakaoOauthProperty(
        String adminKey
) {
}

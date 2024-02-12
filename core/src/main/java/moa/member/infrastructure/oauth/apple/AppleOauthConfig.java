package moa.member.infrastructure.oauth.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth.apple")
public record AppleOauthConfig(
        String keyId,
        String teamId,
        String clientId,
        String privateKeyFileName  // .p8 포함 (ex: aaa.p8)
) {
}

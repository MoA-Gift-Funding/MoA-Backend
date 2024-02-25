package moa.client.wincube.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wincube.auth")
public record WincubeAuthProperty(
        String custId,
        String pwd,
        String autKey,
        String aesKey,
        String rsaPublicKey
) {
}

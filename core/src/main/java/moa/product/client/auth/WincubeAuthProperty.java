package moa.product.client.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wincube.auth")
public record WincubeAuthProperty(
        String custId, // AES256 암호화 대상
        String pwd, // AES256 암호화 대상
        String autKey // AES256 암호화 대상
) {
}

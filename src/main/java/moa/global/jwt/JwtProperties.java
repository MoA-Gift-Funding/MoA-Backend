package moa.global.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(
        String secretKey,
        Long accessTokenExpirationPeriodDay
) {
}

package moa.sms.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sms.nhn")
public record NHNSmsConfig(
        String appKey,
        String secretKey,
        String sendNo
) {
}

package moa.member.infrastructure.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sms.nhn")
public record NHNSmsConfig(
        String appKey,
        String secretKey,
        String sendNo
) {
}

package moa.pay;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tosspayment")
public record PaymentProperty(
        String secretKey
) {
}

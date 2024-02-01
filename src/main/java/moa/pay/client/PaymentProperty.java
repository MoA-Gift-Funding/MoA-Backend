package moa.pay.client;

import static moa.pay.util.Base64Util.parseToBase64;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tosspayment")
public record PaymentProperty(
        String secretKey
) {
    public String basicAuth() {
        return "Basic " + parseToBase64(secretKey);
    }
}

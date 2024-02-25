package moa.client.toss;

import static moa.pay.util.Base64Util.parseToBase64;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tosspayment")
public record TossPaymentProperty(
        String secretKey
) {
    public String basicAuth() {
        return "Basic " + parseToBase64(secretKey);
    }
}

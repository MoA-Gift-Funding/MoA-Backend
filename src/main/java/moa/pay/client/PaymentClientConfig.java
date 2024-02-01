package moa.pay.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static moa.pay.exception.TossPaymentExceptionType.PAYMENT_ERROR;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.http.HttpInterfaceUtil;
import moa.pay.exception.TossPaymentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentClientConfig {

    @Bean
    public TossClient tossClient() {
        RestClient build = RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, (response, body) -> {
                    String errorInfo = new String(body.getBody().readAllBytes(), UTF_8);
                    throw new TossPaymentException(PAYMENT_ERROR.withDetail(errorInfo));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, TossClient.class);
    }
}

package moa.pay.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static moa.pay.exception.TossPaymentExceptionType.TOSS_API_ERROR;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.http.HttpInterfaceUtil;
import moa.pay.exception.TossPaymentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class PaymentClientConfig {

    @Bean
    public TossClient tossClient() {
        RestClient build = RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, (response, body) -> {
                    String errorInfo = new String(body.getBody().readAllBytes(), UTF_8);
                    throw new TossPaymentException(TOSS_API_ERROR.withDetail(errorInfo));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, TossClient.class);
    }
}

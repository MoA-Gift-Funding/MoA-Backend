package moa.pay.client;

import static moa.pay.exception.TossPaymentExceptionType.TOSS_API_ERROR;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.http.HttpInterfaceUtil;
import moa.pay.exception.TossPaymentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class PaymentClientConfig {

    @Bean
    public TossApiClient tossApiClient() {
        RestClient build = RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Toss API ERROR", responseData);
                    throw new TossPaymentException(TOSS_API_ERROR
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, TossApiClient.class);
    }
}

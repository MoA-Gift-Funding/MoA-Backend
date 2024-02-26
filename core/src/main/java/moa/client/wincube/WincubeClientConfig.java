package moa.client.wincube;

import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;
import static moa.global.config.ProfileConfig.PROD_PROFILE;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.exception.ExternalApiException;
import moa.global.http.HttpInterfaceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WincubeClientConfig {

    private final Environment environment;

    @Bean
    public WincubeApiClient wincubeApiClient() {
        RestClient build = RestClient.builder()
                .baseUrl(baseUrl())
                .requestInterceptor((request, body, execution) -> {
                    String requestBody = new String(body);
                    log.info("Wincube API Call\n -> uri: {}\n -> body: {}", request.getURI(), requestBody);
                    return execution.execute(request, body);
                })
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Wincube API ERROR {}", responseData);
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                }).build();
        return HttpInterfaceUtil.createHttpInterface(build, WincubeApiClient.class);
    }

    private String baseUrl() {
        if (Arrays.asList(environment.getActiveProfiles()).contains(PROD_PROFILE)) {
            return "https://gw-api.giftting.co.kr:4431/media/";
        }
        return "http://dev.giftting.co.kr:8084/media/";
    }
}

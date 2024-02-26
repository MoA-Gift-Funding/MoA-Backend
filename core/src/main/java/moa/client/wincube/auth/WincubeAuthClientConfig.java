package moa.client.wincube.auth;

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
public class WincubeAuthClientConfig {

    private final Environment environment;

    @Bean
    public WincubeAuthApiClient wincubeAuthApiClient() {
        RestClient build = RestClient.builder()
                .baseUrl(baseUrl())
                .requestInterceptor((request, body, execution) -> {
                    String requestBody = new String(body);
                    log.info("Wincube Auth API Call\n -> uri: {}\n -> body: {}", request.getURI(), requestBody);
                    return execution.execute(request, body);
                })
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Wincube AUTH API error {}", responseData);
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, WincubeAuthApiClient.class);
    }

    private String baseUrl() {
        if (Arrays.asList(environment.getActiveProfiles()).contains(PROD_PROFILE)) {
            return "https://auth-api.giftting.co.kr/";
        }
        return "http://dev.giftting.co.kr:6281/";
    }
}

package moa.product.client;

import static moa.global.config.ProfileConfig.PROD_PROFILE;
import static moa.product.exception.ProductExceptionType.PRODUCT_EXTERNAL_API_ERROR;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.http.HttpInterfaceUtil;
import moa.product.exception.ProductException;
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
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Wincube API ERROR", responseData);
                    throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, WincubeApiClient.class);
    }

    private String baseUrl() {
        if (Arrays.asList(environment.getActiveProfiles()).contains(PROD_PROFILE)) {
            return "https://gw-api.giftting.co.kr:4431/media/";
        }
        return "http://dev.giftting.co.kr:8084/media/";
    }
}

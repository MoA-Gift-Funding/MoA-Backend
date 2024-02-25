package moa.product.client.auth;

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
public class WincubeAuthClientConfig {

    private final Environment environment;

    @Bean
    public moa.product.client.auth.WincubeAuthApiClient wincubeAuthApiClient() {
        RestClient build = RestClient.builder()
                .baseUrl(baseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Wincube AUTH API ERROR {}", responseData);
                    throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, moa.product.client.auth.WincubeAuthApiClient.class);
    }

    private String baseUrl() {
        if (Arrays.asList(environment.getActiveProfiles()).contains(PROD_PROFILE)) {
            return "https://auth-api.giftting.co.kr/";
        }
        return "http://dev.giftting.co.kr:6281/";
    }
}

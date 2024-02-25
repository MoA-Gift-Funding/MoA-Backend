package moa.client.wincube.auth;

import static moa.global.config.ProfileConfig.PROD_PROFILE;
import static moa.product.exception.ProductExceptionType.PRODUCT_EXTERNAL_API_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.wincube.auth.request.WincubeAuthResultCodeResponse;
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

    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Bean
    public WincubeAuthApiClient wincubeAuthApiClient() {
        RestClient build = RestClient.builder()
                .baseUrl(baseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Wincube AUTH API error {}", responseData);
                    throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .defaultStatusHandler(
                        HttpStatusCode::is2xxSuccessful, (request, response) -> {
                            String responseData = new String(response.getBody().readAllBytes());
                            var resultCode = objectMapper.readValue(
                                    responseData,
                                    WincubeAuthResultCodeResponse.class
                            );
                            if (resultCode.resultCode() >= 400) {
                                log.error("Wincube AUTH API error {}", responseData);
                                throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                                        .withDetail(responseData)
                                        .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                            }
                            log.info("Wincube AUTH API response {}", responseData);
                        }
                )
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

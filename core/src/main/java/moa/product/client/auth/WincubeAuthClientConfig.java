package moa.product.client.auth;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.http.HttpInterfaceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WincubeAuthClientConfig {

    private final Environment environment;

    @Bean
    public WincubeAuthApiClient wincubeApiClient() {
        RestClient build = RestClient.builder()
                .baseUrl(baseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    log.error("Wincube API ERROR", new String(response.getBody().readAllBytes()));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, WincubeAuthApiClient.class);
    }

    private String baseUrl() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return "https://auth-api.giftting.co.kr/";
        }
        return "http://dev.giftting.co.kr:6281/";
    }
}

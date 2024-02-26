package moa.client.webhook;


import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.exception.ExternalApiException;
import moa.global.http.HttpInterfaceUtil;
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
public class WebHookClientConfig {

    @Bean
    public DiscordWebHookApiClient webHookApiClient() {
        RestClient build = RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Discord WebHook API ERROR {}", responseData);
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(build, DiscordWebHookApiClient.class);
    }
}

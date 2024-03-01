package moa.client.oauth.naver;


import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.discord.DiscordWebHookClient;
import moa.client.exception.ExternalApiException;
import moa.global.http.HttpInterfaceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NaverClientConfig {

    private final DiscordWebHookClient discordWebHookClient;

    @Bean
    public NaverApiClient naverApiClient() {
        RestClient restClient = RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("Naver API ERROR {}", responseData);
                    discordWebHookClient.sendToErrorChannel("Naver API ERROR \n ->" + responseData);
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return HttpInterfaceUtil.createHttpInterface(restClient, NaverApiClient.class);
    }
}

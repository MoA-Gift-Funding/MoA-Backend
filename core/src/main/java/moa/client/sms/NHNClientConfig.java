package moa.client.sms;

import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;
import static moa.global.http.HttpInterfaceUtil.createHttpInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.discord.DiscordWebHookClient;
import moa.client.exception.ExternalApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NHNClientConfig {

    private final DiscordWebHookClient discordWebHookClient;

    @Bean
    public NHNApiClient nhnApiClient() {
        RestClient restClient = RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    log.error("NHN SMS API ERROR {}", responseData);
                    discordWebHookClient.sendToErrorChannel("NHN SMS API ERROR \n ->" + responseData);
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                            .withDetail(responseData)
                            .setStatus(HttpStatus.valueOf(response.getStatusCode().value())));
                })
                .build();
        return createHttpInterface(restClient, NHNApiClient.class);
    }
}

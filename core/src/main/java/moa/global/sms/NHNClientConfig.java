package moa.global.sms;

import static moa.global.http.HttpInterfaceUtil.createHttpInterface;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
public class NHNClientConfig {

    private final RestClient restClient;

    @Bean
    public NHNApiClient nhnApiClient() {
        return createHttpInterface(restClient, NHNApiClient.class);
    }
}
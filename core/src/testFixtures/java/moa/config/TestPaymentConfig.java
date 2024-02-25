package moa.config;

import static org.mockito.Mockito.mock;

import moa.client.toss.TossClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestPaymentConfig {

    @Bean
    public TossClient tossClient() {
        return mock(TossClient.class);
    }
}

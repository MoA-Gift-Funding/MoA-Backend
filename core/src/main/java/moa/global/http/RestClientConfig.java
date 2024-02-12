package moa.global.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * RestClient 는 Thread-Safe
     * <p>
     * Once created (or built), the RestClient can be used safely by multiple threads.
     * [https://docs.spring.io/spring-framework/reference/integration/rest-clients.html]
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}

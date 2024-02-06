package moa.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Profile("!test")
@EnableAsync
@Configuration
public class AsyncConfig {
}

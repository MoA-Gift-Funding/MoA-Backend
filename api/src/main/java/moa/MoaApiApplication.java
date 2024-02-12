package moa;

import moa.global.config.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@PropertySource(
        value = {"classpath:application-core-${spring.profiles.active}.yml"},
        factory = YamlPropertySourceFactory.class
)
@SpringBootApplication
@ConfigurationPropertiesScan
public class MoaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoaApiApplication.class, args);
    }
}

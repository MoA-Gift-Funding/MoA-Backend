package moa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoaApplication.class, args);
    }
}

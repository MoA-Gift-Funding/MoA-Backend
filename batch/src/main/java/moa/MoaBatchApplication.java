package moa;

import java.time.LocalDateTime;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import moa.global.config.YamlPropertySourceFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(
        value = {"classpath:application-core-${spring.profiles.active}.yml"},
        factory = YamlPropertySourceFactory.class
)
@SpringBootApplication
@ConfigurationPropertiesScan
public class MoaBatchApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(MoaBatchApplication.class, args);
    }

    @Component
    @RequiredArgsConstructor
    public static class ProductInitializer implements CommandLineRunner {

        private final JobLauncher jobLauncher;

        private final Job wincubeProductUpdateJob;

        @Override
        public void run(String... args) throws Exception {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("now", LocalDateTime.now())
                    .toJobParameters();
            jobLauncher.run(wincubeProductUpdateJob, jobParameters);
        }
    }
}

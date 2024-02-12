package moa;

import moa.MoaApplication.YamlPropertySourceFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

@PropertySource(
        value = {"classpath:application-core-${spring.profiles.active}.yml"},
        factory = YamlPropertySourceFactory.class
)
@SpringBootApplication
@ConfigurationPropertiesScan
public class MoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoaApplication.class, args);
    }

    static class YamlPropertySourceFactory extends DefaultPropertySourceFactory {
        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(
                String name,
                EncodedResource resource
        ) {
            var factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            return new PropertiesPropertySource(resource.getResource().getFilename(), factory.getObject());
        }
    }
}

package moa.client.wincube;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wincube")
public record WincubeProperty(
        String mdCode,
        String callback
) {
}

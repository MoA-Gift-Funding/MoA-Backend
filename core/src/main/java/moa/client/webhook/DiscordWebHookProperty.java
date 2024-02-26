package moa.client.webhook;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public record DiscordWebHookProperty(
        String webhookId,
        String webhookToken
) {
}

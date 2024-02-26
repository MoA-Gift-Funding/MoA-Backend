package moa.client.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public record DiscordWebHookProperty(
        String webhookId,
        String webhookToken
) {
}

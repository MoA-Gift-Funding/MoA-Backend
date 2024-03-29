package moa.client.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public record DiscordWebHookProperty(
        WebhookProperty inquiryChannel,
        WebhookProperty errorChannel,
        WebhookProperty reportChannel
) {
    public record WebhookProperty(
            String webhookId,
            String webhookToken
    ) {
    }
}

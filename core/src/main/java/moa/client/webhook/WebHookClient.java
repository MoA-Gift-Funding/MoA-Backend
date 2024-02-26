package moa.client.webhook;

import lombok.RequiredArgsConstructor;
import moa.client.webhook.dto.DiscordSendMessageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebHookClient {

    private final DiscordWebHookProperty discordWebHookProperty;
    private final DiscordWebHookApiClient webHookApiClient;

    public void sendContent(String content) {
        webHookApiClient.sendMessage(
                discordWebHookProperty.webhookId(),
                discordWebHookProperty.webhookToken(),
                new DiscordSendMessageRequest(content)
        );
    }
}

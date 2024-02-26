package moa.client.discord;

import lombok.RequiredArgsConstructor;
import moa.client.discord.dto.DiscordSendMessageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordWebHookClient {

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

package moa.client.discord;

import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;

import lombok.RequiredArgsConstructor;
import moa.client.discord.dto.DiscordSendMessageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Async(VIRTUAL_THREAD_EXECUTOR)
public class DiscordWebHookClient {

    private final DiscordWebHookProperty discordWebHookProperty;
    private final DiscordWebHookApiClient webHookApiClient;

    public void sendToInquiryChannel(String content) {
        webHookApiClient.sendMessage(
                discordWebHookProperty.inquiryChannel().webhookId(),
                discordWebHookProperty.inquiryChannel().webhookToken(),
                new DiscordSendMessageRequest(content)
        );
    }

    public void sendToErrorChannel(String content) {
        webHookApiClient.sendMessage(
                discordWebHookProperty.errorChannel().webhookId(),
                discordWebHookProperty.errorChannel().webhookToken(),
                new DiscordSendMessageRequest(content)
        );
    }
}

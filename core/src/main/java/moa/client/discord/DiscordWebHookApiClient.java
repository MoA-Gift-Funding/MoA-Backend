package moa.client.discord;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import moa.client.discord.dto.DiscordSendMessageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("https://discord.com/api")
public interface DiscordWebHookApiClient {

    /**
     * https://discord.com/developers/docs/resources/webhook
     */
    @PostExchange(url = "/webhooks/{webhookId}/{webhookToken}", contentType = APPLICATION_JSON_VALUE)
    Void sendMessage(
            @PathVariable("webhookId") String webhookId,
            @PathVariable("webhookToken") String webhookToken,
            @RequestBody DiscordSendMessageRequest request
    );
}

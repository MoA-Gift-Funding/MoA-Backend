package moa.client.webhook;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import moa.client.webhook.dto.DiscordSendMessageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

// https://discord.com/developers/docs/resources/webhook
@HttpExchange("https://discord.com/api/webhooks")
public interface DiscordWebHookApiClient {

    @PostExchange(url = "/{webhookId}/{webhookToken}", contentType = APPLICATION_JSON_VALUE)
    Void sendMessage(
            @PathVariable("webhookId") String webhookId,
            @PathVariable("webhookToken") String webhookToken,
            @RequestBody DiscordSendMessageRequest request
    );
}

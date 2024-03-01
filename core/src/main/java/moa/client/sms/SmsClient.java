package moa.client.sms;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.discord.DiscordWebHookClient;
import moa.client.sms.NHNSendSmsRequest.RecipientRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsClient {

    private final NHNApiClient client;
    private final NHNSmsConfig config;
    private final DiscordWebHookClient discordWebHookClient;

    public void send(String message, String phoneNumber) {
        NHNSendSmsRequest request = new NHNSendSmsRequest(
                message,
                config.sendNo(),
                List.of(new RecipientRequest(phoneNumber))
        );

        NHNSendSmsResponse response = client.sendSms(config.appKey(), config.secretKey(), request);
        if (response.header().isSuccessful()) {
            log.info("NHN 문자 발송 성공 {}", response);
        } else {
            log.error("NHN 문자 발송 API 에서 문제 발생 {}", response);
            discordWebHookClient.sendToErrorChannel("NHN 문자 발송 API 에서 문제 발생 \n ->" + response);
            throw new RuntimeException("NHN 문자 발송 API 에서 문제 발생. Detail: %s".formatted(response.toString()));
        }
    }
}

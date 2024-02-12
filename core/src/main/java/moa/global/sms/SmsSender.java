package moa.global.sms;

import static moa.member.exception.MemberExceptionType.FAILED_SEND_PHONE_VERIFICATION_NUMBER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.sms.NHNSendSmsRequest.RecipientRequest;
import moa.member.exception.MemberException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsSender {

    private final NHNApiClient client;
    private final NHNSmsConfig config;

    public void send(String message, String phoneNumbers) {
        send(message, List.of(phoneNumbers));
    }

    public void send(String message, List<String> phoneNumbers) {
        List<RecipientRequest> recipientNoList = phoneNumbers.stream()
                .map(RecipientRequest::new)
                .toList();
        NHNSendSmsRequest request = new NHNSendSmsRequest(
                "[모아] " + message,
                config.sendNo(),
                recipientNoList
        );
        NHNSendSmsResponse response = client.sendSms(config.appKey(), config.secretKey(), request);
        if (!response.header().isSuccessful()) {
            log.error("NHN 문자 발송 API 에서 문제 발생 {}", response);
            throw new MemberException(FAILED_SEND_PHONE_VERIFICATION_NUMBER);
        } else {
            log.info("NHN 문자 발송 성공 {}", response);
        }
    }
}

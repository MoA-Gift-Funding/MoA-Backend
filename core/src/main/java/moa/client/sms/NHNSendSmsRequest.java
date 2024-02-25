package moa.client.sms;

import java.util.List;

public record NHNSendSmsRequest(
        String body,
        String sendNo,
        List<RecipientRequest> recipientList
) {
    public record RecipientRequest(
            String recipientNo
    ) {
    }
}

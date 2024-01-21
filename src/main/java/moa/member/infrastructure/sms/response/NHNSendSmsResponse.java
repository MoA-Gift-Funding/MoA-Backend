package moa.member.infrastructure.sms.response;

import java.util.List;

public record NHNSendSmsResponse(
        Header header,
        Body body
) {
    public record Header(
            boolean isSuccessful,
            int resultCode,
            String resultMessage
    ) {
    }

    public record Body(
            Data data
    ) {
        public record Data(
                String requestId,
                String statusCode,
                String senderGroupingKey,
                List<SendResult> sendResultList
        ) {
            public record SendResult(
                    String recipientNo,
                    int resultCode,
                    String resultMessage,
                    int recipientSeq,
                    String recipientGroupingKey
            ) {
            }
        }
    }
}

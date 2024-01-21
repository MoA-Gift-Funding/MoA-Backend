package moa.member.infrastructure.sms;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberSender;
import moa.member.exception.MemberException;
import moa.member.exception.MemberExceptionType;
import moa.member.infrastructure.sms.request.NHNSendSmsRequest;
import moa.member.infrastructure.sms.request.NHNSendSmsRequest.RecipientRequest;
import moa.member.infrastructure.sms.response.NHNSendSmsResponse;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NHNPhoneVerificationNumberSender implements PhoneVerificationNumberSender {

    private final NHNApiClient nhnApiClient;
    private final NHNSmsConfig nhnSmsConfig;

    @Override
    public void sendVerificationNumber(Phone phone, PhoneVerificationNumber verificationNumber) {
        String message = "인증번호는 [" + verificationNumber.value() + "] 입니다.";
        NHNSendSmsRequest request = new NHNSendSmsRequest(
                message,
                nhnSmsConfig.sendNo(),
                List.of(new RecipientRequest(phone.getPhoneNumber()))
        );
        NHNSendSmsResponse response = nhnApiClient.sendSms(nhnSmsConfig.appKey(), nhnSmsConfig.secretKey(), request);
        if (!response.header().isSuccessful()) {
            log.error("NHN 문자 발송 API 에서 문제 발생 {}", response);
            throw new MemberException(MemberExceptionType.FAILED_SEND_PHONE_VERIFICATION_NUMBER);
        } else {
            log.info("NHN 문자 발송 성공 {}", response);
        }
    }
}

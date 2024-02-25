package moa.member.infrastructure.sms;

import static moa.member.exception.MemberExceptionType.FAILED_SEND_PHONE_VERIFICATION_NUMBER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.sms.SmsClient;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberSender;
import moa.member.exception.MemberException;
import moa.sms.SmsMessageFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NHNPhoneVerificationNumberSender implements PhoneVerificationNumberSender {

    private final SmsClient sender;
    private final SmsMessageFactory messageFactory;

    @Override
    public void sendVerificationNumber(Phone phone, PhoneVerificationNumber verificationNumber) {
        String message = messageFactory.generatePhoneVerificationMessage(verificationNumber.value());
        try {
            sender.send(message, phone.getPhoneNumber());
        } catch (Exception e) {
            throw new MemberException(FAILED_SEND_PHONE_VERIFICATION_NUMBER);
        }
    }
}

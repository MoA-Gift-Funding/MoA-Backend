package moa.member.infrastructure.sms;

import static moa.member.exception.MemberExceptionType.FAILED_SEND_PHONE_VERIFICATION_NUMBER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.sms.SmsSender;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberSender;
import moa.member.exception.MemberException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NHNPhoneVerificationNumberSender implements PhoneVerificationNumberSender {

    private final SmsSender sender;

    @Override
    public void sendVerificationNumber(Phone phone, PhoneVerificationNumber verificationNumber) {
        String message = "인증번호는 [" + verificationNumber.value() + "] 입니다.";
        try {
            sender.send(message, phone.getPhoneNumber());
        } catch (Exception e) {
            throw new MemberException(FAILED_SEND_PHONE_VERIFICATION_NUMBER);
        }
    }
}

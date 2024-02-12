package moa.member.domain.phone;

public interface PhoneVerificationNumberSender {

    void sendVerificationNumber(Phone phone, PhoneVerificationNumber verificationNumber);
}

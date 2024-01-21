package moa.member.domain.phone;


import static moa.member.exception.MemberExceptionType.DIFFERENT_PHONE_VERIFICATION_NUMBER;

import moa.member.exception.MemberException;

public record PhoneVerificationNumber(
        String value
) {
    public PhoneVerificationNumber {
        validate(value);
    }

    private void validate(String number) {
        if (number != null && number.length() == 6 && number.matches("\\d+")) {
            return;
        }
        throw new IllegalArgumentException("Invalid phone verification number");
    }

    public void verify(String verificationNumber) {
        if (!value.equals(verificationNumber)) {
            throw new MemberException(DIFFERENT_PHONE_VERIFICATION_NUMBER);
        }
    }
}

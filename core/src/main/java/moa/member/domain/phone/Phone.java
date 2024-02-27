package moa.member.domain.phone;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.MemberValidator;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Phone {

    @Transient
    private Member member;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(name = "verified_phone", nullable = true)
    private boolean verified;

    @Column(nullable = true)
    private String deviceToken;

    public Phone(Member member, String phoneNumber) {
        this.member = member;
        this.phoneNumber = phoneNumber;
    }

    public void verify() {
        this.verified = true;
    }

    public PhoneVerificationNumber generateVerification(MemberValidator memberValidator) {
        memberValidator.validateDuplicatedVerifiedPhone(this);
        return generateVerification();
    }

    private PhoneVerificationNumber generateVerification() {
        int leftLimit = 48;  // numeral '0'
        int rightLimit = 57;  // numeral '9'
        int targetStringLength = 6;
        return new PhoneVerificationNumber(
                ThreadLocalRandom.current()
                        .ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
        );
    }

    public void permitNotification(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void rejectNotification() {
        this.deviceToken = null;
    }
}

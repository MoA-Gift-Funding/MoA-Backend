package moa.member.domain.phone;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
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

    public PhoneVerificationNumber generateVerification(
            MemberValidator memberValidator,
            VerificationNumberGenerator generator
    ) {
        memberValidator.validateDuplicatedVerifiedPhone(this);
        return generateVerification(generator);
    }

    private PhoneVerificationNumber generateVerification(VerificationNumberGenerator generator) {
        return generator.generate();
    }

    public void permitNotification(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void rejectNotification() {
        this.deviceToken = null;
    }
}

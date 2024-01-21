package moa.member.domain.phone;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.MemberValidator;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Embeddable
public class Phone {

    private static final Random RANDOM = new Random();

    @Transient
    private Member member;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(name = "verified_phone")
    private boolean verified;

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
                RANDOM.ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
        );
    }
}

package moa.member.domain;

import static moa.member.exception.MemberExceptionType.ALREADY_EXISTS_EMAIL;
import static moa.member.exception.MemberExceptionType.ALREADY_EXISTS_PHONE;

import lombok.RequiredArgsConstructor;
import moa.member.domain.phone.Phone;
import moa.member.exception.MemberException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    public void validateDuplicatedEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ALREADY_EXISTS_EMAIL);
        }
    }

    public void validateDuplicatedVerifiedPhone(Phone phone) {
        if (memberRepository.existsByVerifiedPhone(phone.getPhoneNumber())) {
            throw new MemberException(ALREADY_EXISTS_PHONE);
        }
    }
}

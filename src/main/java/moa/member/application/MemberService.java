package moa.member.application;


import lombok.RequiredArgsConstructor;
import moa.member.application.command.MemberUpdateCommand;
import moa.member.application.command.SignupCommand;
import moa.member.application.command.VerifyPhoneCommand;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.MemberValidator;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneRepository;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberRepository;
import moa.member.domain.phone.PhoneVerificationNumberSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;
    private final PhoneRepository phoneRepository;
    private final PhoneVerificationNumberSender sender;
    private final PhoneVerificationNumberRepository phoneVerificationNumberRepository;

    public void sendPhoneVerificationNumber(Long memberId, String phoneNumber) {
        Member member = memberRepository.getById(memberId);
        Phone phone = new Phone(member, phoneNumber);
        PhoneVerificationNumber verificationNumber = phone.generateVerification(memberValidator);
        phoneRepository.save(phone);
        phoneVerificationNumberRepository.save(member, verificationNumber);
        sender.sendVerificationNumber(phone, verificationNumber);
    }

    public void verifyPhone(VerifyPhoneCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PhoneVerificationNumber verificationNumber = phoneVerificationNumberRepository.getByMember(member);
        verificationNumber.verify(command.verificationNumber());
        Phone phone = phoneRepository.getByMember(member);
        phone.verify();
        member.changeVerifiedPhone(memberValidator, phone);
    }

    public void signup(SignupCommand command) {
        Member member = memberRepository.getById(command.memberId());
        member.signup(
                memberValidator,
                command.email(),
                command.nickname(),
                command.birthday(),
                command.birthyear(),
                command.profileImageUrl()
        );
    }

    public void update(MemberUpdateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        member.update(
                command.nickname(),
                command.birthyear(),
                command.birthday(),
                command.profileImageUrl()
        );
    }
}

package moa.member.application;


import lombok.RequiredArgsConstructor;
import moa.member.application.command.MemberUpdateCommand;
import moa.member.application.command.PhoneVerifyCommand;
import moa.member.application.command.SignupCommand;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.MemberValidator;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClientComposite;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneRepository;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberRepository;
import moa.member.domain.phone.PhoneVerificationNumberSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberValidator memberValidator;
    private final PhoneRepository phoneRepository;
    private final MemberRepository memberRepository;
    private final PhoneVerificationNumberSender sender;
    private final TransactionTemplate transactionTemplate;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final PhoneVerificationNumberRepository phoneVerificationNumberRepository;

    public Long login(OauthProvider provider, String accessToken) {
        Member member = oauthMemberClientComposite.fetch(provider, accessToken);
        return memberRepository.findByOauthId(member.getOauthId())
                .orElseGet(() -> preSignup(member))
                .getId();
    }

    private Member preSignup(Member member) {
        return transactionTemplate.execute(status -> {
                    member.preSignup(memberValidator);
                    return memberRepository.save(member);
                }
        );
    }

    @Transactional
    public void sendPhoneVerificationNumber(Long memberId, String phoneNumber) {
        Member member = memberRepository.getById(memberId);
        Phone phone = new Phone(member, phoneNumber);
        PhoneVerificationNumber verificationNumber = phone.generateVerification(memberValidator);
        phoneRepository.save(phone);
        phoneVerificationNumberRepository.save(member, verificationNumber);
        sender.sendVerificationNumber(phone, verificationNumber);
    }

    @Transactional
    public void verifyPhone(PhoneVerifyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PhoneVerificationNumber verificationNumber = phoneVerificationNumberRepository.getByMember(member);
        verificationNumber.verify(command.verificationNumber());
        Phone phone = phoneRepository.getByMember(member);
        phone.verify();
        member.changeVerifiedPhone(memberValidator, phone);
    }

    @Transactional
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

    @Transactional
    public void update(MemberUpdateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        member.update(
                command.nickname(),
                command.birthyear(),
                command.birthday(),
                command.profileImageUrl()
        );
    }

    @Transactional
    public void permitNotification(Long memberId, String deviceToken) {
        Member member = memberRepository.getById(memberId);
        member.permitNotification(deviceToken);
    }

    @Transactional
    public void rejectNotification(Long memberId) {
        Member member = memberRepository.getById(memberId);
        member.rejectNotification();
    }

    public void withdraw(Long memberId, OauthProvider provider, String accessToken) {
        Member member = memberRepository.getById(memberId);
        oauthMemberClientComposite.withdraw(provider, accessToken);
        transactionTemplate.executeWithoutResult(status -> {
            member.withdraw();
            memberRepository.save(member);
        });
    }
}

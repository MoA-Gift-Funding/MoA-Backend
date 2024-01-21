package moa.member.domain.phone;


import moa.member.domain.Member;

public interface PhoneVerificationNumberRepository {

    int VERIFICATION_NUMBER_TIMEOUT = 3;

    void save(Member member, PhoneVerificationNumber phoneVerificationNumber);

    PhoneVerificationNumber getByMember(Member member);
}

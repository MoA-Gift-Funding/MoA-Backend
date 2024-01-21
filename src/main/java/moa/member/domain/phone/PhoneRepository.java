package moa.member.domain.phone;


import moa.member.domain.Member;

public interface PhoneRepository {

    int PHONE_TIMEOUT = 5;

    void save(Phone phone);

    Phone getByMember(Member member);
}

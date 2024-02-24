package moa.cs.application;

import lombok.RequiredArgsConstructor;
import moa.cs.application.command.WriteInquiryCommand;
import moa.cs.domain.PersonalInquiry;
import moa.cs.domain.PersonalInquiryRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalInquiryService {

    private final MemberRepository memberRepository;
    private final PersonalInquiryRepository personalInquiryRepository;

    public Long inquire(WriteInquiryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PersonalInquiry inquire = command.toInquire(member);
        return personalInquiryRepository.save(inquire)
                .getId();
    }
}

package moa.customerservicecenter.application.command;

import moa.customerservicecenter.domain.PersonalInquiry;
import moa.customerservicecenter.domain.QuestionCategory;
import moa.member.domain.Member;

public record WriteInquiryCommand(
        Long memberId,
        QuestionCategory category,
        String content
) {
    public PersonalInquiry toInquire(Member member) {
        return new PersonalInquiry(
                category,
                content,
                member
        );
    }
}

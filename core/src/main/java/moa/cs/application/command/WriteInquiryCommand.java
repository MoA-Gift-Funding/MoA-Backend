package moa.cs.application.command;

import moa.cs.domain.PersonalInquiry;
import moa.cs.domain.QuestionCategory;
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

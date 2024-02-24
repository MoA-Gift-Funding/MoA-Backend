package moa.cs.query.response;

import moa.cs.domain.PersonalInquiry;
import moa.cs.domain.QuestionCategory;

public record PersonalInquiryResponse(
        Long id,
        QuestionCategory category,
        String content,
        String answer
) {
    public static PersonalInquiryResponse from(PersonalInquiry inquiry) {
        return new PersonalInquiryResponse(
                inquiry.getId(),
                inquiry.getCategory(),
                inquiry.getContent(),
                inquiry.getAnswer()
        );
    }
}

package moa.customerservicecenter.query.response;

import moa.customerservicecenter.domain.PersonalInquiry;
import moa.customerservicecenter.domain.QuestionCategory;

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

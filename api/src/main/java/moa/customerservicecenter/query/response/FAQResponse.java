package moa.customerservicecenter.query.response;

import moa.customerservicecenter.domain.FAQ;
import moa.customerservicecenter.domain.QuestionCategory;

public record FAQResponse(
        Long id,
        QuestionCategory category,
        String content,
        String answer
) {
    public static FAQResponse from(FAQ faq) {
        return new FAQResponse(
                faq.getId(),
                faq.getCategory(),
                faq.getContent(),
                faq.getAnswer()
        );
    }
}

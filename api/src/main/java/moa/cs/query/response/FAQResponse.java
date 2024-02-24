package moa.cs.query.response;

import moa.cs.domain.FAQ;
import moa.cs.domain.QuestionCategory;

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

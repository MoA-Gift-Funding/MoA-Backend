package moa.customerservicecenter.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import moa.customerservicecenter.application.command.WriteInquiryCommand;
import moa.customerservicecenter.domain.QuestionCategory;

public record WriteInquiryRequest(
        @NotNull QuestionCategory category,
        @NotBlank String content
) {
    public WriteInquiryCommand toCommand(Long memberId) {
        return new WriteInquiryCommand(
                memberId,
                category,
                content
        );
    }
}

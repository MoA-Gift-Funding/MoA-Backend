package moa.report.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import moa.member.domain.Member;
import moa.report.application.command.ReportWriteCommand;
import moa.report.domain.Report;
import moa.report.domain.Report.DomainType;

public record ReportWriteRequest(
        @Schema(description = "신고하려는 도메인 타입")
        @NotNull DomainType domainType,

        @Schema(description = "해당 도메인의 id")
        @NotNull Long domainId,

        @Schema(description = "신고내용")
        String content
) {
    public Report toReport(Member member) {
        return new Report(
                domainType,
                domainId,
                content,
                member
        );
    }

    public ReportWriteCommand toCommand(Long memberId) {
        return new ReportWriteCommand(
                memberId,
                domainType,
                domainId,
                content
        );
    }
}

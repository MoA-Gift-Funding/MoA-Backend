package moa.report.application.command;

import moa.member.domain.Member;
import moa.report.domain.Report;
import moa.report.domain.Report.DomainType;

public record ReportWriteCommand(
        Long memberId,
        DomainType domainType,
        Long domainId,
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
}

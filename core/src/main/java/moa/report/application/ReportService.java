package moa.report.application;

import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.report.application.command.ReportWriteCommand;
import moa.report.domain.Report;
import moa.report.domain.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    public Long report(ReportWriteCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Report report = command.toReport(member);
        return reportRepository.save(report)
                .getId();
    }
}

package moa.report;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.report.application.ReportService;
import moa.report.request.ReportWriteRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @PostMapping
    public void write(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody ReportWriteRequest request
    ) {
        reportService.report(request.toCommand(memberId));
    }
}

package moa.funding.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.funding.application.FundingService;
import moa.funding.domain.Funding;
import moa.funding.presentation.request.FundingCreateRequest;
import static moa.member.domain.MemberStatus.SIGNED_UP;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fundings")
public class FundingController implements FundingApi {

    private final FundingService fundingService;

    @PostMapping
    public ResponseEntity<Void> createFunding(
        @Auth(permit = {SIGNED_UP}) Long memberId,
        @Valid @RequestBody FundingCreateRequest request
    ) {
        Funding funding = fundingService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/fundings/" + funding.getId())).build();
    }
}

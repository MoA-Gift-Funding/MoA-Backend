package moa.funding.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.funding.application.FundingService;
import moa.funding.presentation.request.FundingCreateRequest;
import moa.funding.query.FundingQueryService;
import moa.funding.query.response.MyFundingsResponse.MyFundingDetail;
import moa.global.presentation.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fundings")
public class FundingController implements FundingApi {

    private final FundingService fundingService;
    private final FundingQueryService fundingQueryService;

    @PostMapping
    public ResponseEntity<Void> createFunding(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody FundingCreateRequest request
    ) {
        Long fundingId = fundingService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/fundings/" + fundingId)).build();
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<MyFundingDetail>> findFunding(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        var result = PageResponse.from(fundingQueryService.findMyFundings(memberId, pageable));
        return ResponseEntity.ok(result);
    }
}
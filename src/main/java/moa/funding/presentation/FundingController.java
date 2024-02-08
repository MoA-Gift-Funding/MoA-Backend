package moa.funding.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.funding.application.FundingService;
import moa.funding.domain.FundingStatus;
import moa.funding.presentation.request.FundingCreateRequest;
import moa.funding.presentation.request.FundingFinishRequest;
import moa.funding.presentation.request.FundingParticipateRequest;
import moa.funding.query.FundingQueryService;
import moa.funding.query.response.FundingDetailResponse;
import moa.funding.query.response.FundingMessageResponse;
import moa.funding.query.response.FundingResponse;
import moa.funding.query.response.MyFundingsResponse.MyFundingDetail;
import moa.global.presentation.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/{id}/participate")
    public ResponseEntity<Void> participate(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable Long id,
            @Valid @RequestBody FundingParticipateRequest request
    ) {
        fundingService.participate(request.toCommand(id, memberId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Void> finish(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable Long id,
            @Valid @RequestBody FundingFinishRequest request
    ) {
        fundingService.finish(request.toCommand(id, memberId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable Long id
    ) {
        fundingService.cancel(id, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/participate/cancel")
    public ResponseEntity<Void> participateCancel(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable Long id
    ) {
        fundingService.participateCancel(id, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<MyFundingDetail>> findMyFundings(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        var result = fundingQueryService.findMyFundings(memberId, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundingDetailResponse> findFundingDetail(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable Long id
    ) {
        var result = fundingQueryService.findFundingById(memberId, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<PageResponse<FundingResponse>> findFundings(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @RequestParam(value = "statuses", defaultValue = "PROCESSING") List<FundingStatus> statuses,
            @PageableDefault(size = 10, sort = "endDate", direction = ASC) Pageable pageable
    ) {
        var result = fundingQueryService.findFundings(memberId, statuses, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @GetMapping("/messages")
    public ResponseEntity<PageResponse<FundingMessageResponse>> findFundings(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        var result = fundingQueryService.findMessages(memberId, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }
}

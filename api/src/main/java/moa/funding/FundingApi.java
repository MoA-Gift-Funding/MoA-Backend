package moa.funding;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import moa.auth.Auth;
import moa.funding.domain.FundingStatus;
import moa.funding.query.response.FundingDetailResponse;
import moa.funding.query.response.FundingMessageResponse;
import moa.funding.query.response.FundingResponse;
import moa.funding.query.response.MyFundingsResponse.MyFundingResponse;
import moa.funding.query.response.ParticipatedFundingResponse;
import moa.funding.request.FundingCreateRequest;
import moa.funding.request.FundingFinishRequest;
import moa.funding.request.FundingParticipateCancelRequest;
import moa.funding.request.FundingParticipateRequest;
import moa.global.presentation.PageResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "펀딩 API", description = "펀딩 관련 API")
@SecurityRequirement(name = "JWT")
public interface FundingApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "펀딩 생성")
    @PostMapping
    ResponseEntity<Void> createFunding(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Valid @RequestBody FundingCreateRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "500"),
            }
    )
    @Operation(summary = "펀딩 참여")
    @PostMapping("/{id}/participate")
    ResponseEntity<Void> participate(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "펀딩 ID")
            @PathVariable Long id,

            @Valid @RequestBody FundingParticipateRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "500"),
            }
    )
    @Operation(summary = "펀딩 끝내기")
    @PostMapping("/{id}/finish")
    ResponseEntity<Void> finish(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "펀딩 ID")
            @PathVariable Long id,

            @Valid @RequestBody FundingFinishRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "펀딩 취소")
    @PostMapping("/{id}/cancel")
    ResponseEntity<Void> cancel(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "펀딩 ID")
            @PathVariable Long id
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "펀딩 참여 취소")
    @PostMapping("/{id}/participate/cancel")
    ResponseEntity<Void> participateCancel(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "펀딩 ID")
            @PathVariable Long id,

            @Valid @RequestBody FundingParticipateCancelRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "내가 개설한 펀딩 조회")
    @GetMapping("/my")
    ResponseEntity<PageResponse<MyFundingResponse>> findMyFundings(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @ParameterObject
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "펀딩 상세 조회")
    @GetMapping("/{fundingId}")
    ResponseEntity<FundingDetailResponse> findFundingDetail(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "펀딩 ID")
            @PathVariable Long fundingId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "친구들의 펀딩 목록 조회", description = "내 펀딩은 조회되지 않음")
    @GetMapping
    ResponseEntity<PageResponse<FundingResponse>> findFriendsFundings(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = QUERY, description = "조회될 펀딩 상태들 (기본값 PROCESSING)", example = "PROCESSING,DELIVERY_WAITING")
            @RequestParam(value = "statuses", defaultValue = "PROCESSING") List<FundingStatus> statuses,

            @ParameterObject
            @PageableDefault(size = 10, sort = "endDate", direction = ASC) Pageable pageable
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "내가 참여한 펀딩 목록 조회")
    @GetMapping("/participated")
    ResponseEntity<PageResponse<ParticipatedFundingResponse>> findParticipatedFundings(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @ParameterObject
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "내가 받은 펀딩 메세지 목록 조회")
    @GetMapping("/messages")
    ResponseEntity<PageResponse<FundingMessageResponse>> findReceivedFundingMessages(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @ParameterObject
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable
    );
}

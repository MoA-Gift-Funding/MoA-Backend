package moa.funding.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.auth.Auth;
import moa.funding.presentation.request.FundingCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "펀딩 API", description = "펀딩 관련 API")
@SecurityRequirement(name = "JWT")
public interface FundingApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "회원가입되지 않은 회원의 경우(임시 회원가입인 경우도 해당 케이스)"),
            }
    )
    @Operation(summary = "펀딩 생성")
    @PostMapping
    ResponseEntity<Void> createFunding(
            @Parameter(hidden = true) @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody FundingCreateRequest request
    );
}

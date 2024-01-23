package moa.funding.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.funding.presentation.request.FundingCreateRequest;

@Tag(name = "펀딩 API", description = "펀딩 관련 API")
@SecurityRequirement(name = "JWT")
public interface FundingApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
        }
    )
    @Operation(summary = "펀딩 생성")
    @PostMapping
    ResponseEntity<Void> createFunding(
        @Parameter(hidden = true) Long memberId,
        @Valid @RequestBody FundingCreateRequest request
    );
}

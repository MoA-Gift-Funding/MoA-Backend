package moa.global.s3;


import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.auth.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Presigned Url API", description = "이미지 업로드용 Presigned Url API")
@SecurityRequirement(name = "JWT")
public interface PresignedUrlApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "403"),
            }
    )
    @Operation(summary = "Presigned Url API")
    @PostMapping
    ResponseEntity<CreatePresignedUrlResponse> createPresignedUrl(
            @Parameter(hidden = true) @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId,
            @Valid @RequestBody CreatePresignedUrlRequest request
    );
}

package moa.customerservicecenter;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import moa.auth.Auth;
import moa.customerservicecenter.query.response.PersonalInquiryResponse;
import moa.customerservicecenter.request.WriteInquiryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "1대1 문의 API", description = "1대1 문의 관련 API")
@SecurityRequirement(name = "JWT")
public interface PersonalInquiryApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "1대1 문의 작성")
    @PostMapping
    ResponseEntity<Void> inquire(
            @Auth(permit = SIGNED_UP) Long memberId,
            @Valid @RequestBody WriteInquiryRequest request
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
    @Operation(summary = "내가 작성한 1대1 문의 전체 조회")
    @GetMapping("/my")
    ResponseEntity<List<PersonalInquiryResponse>> findMy(
            @Auth(permit = SIGNED_UP) Long memberId
    );
}

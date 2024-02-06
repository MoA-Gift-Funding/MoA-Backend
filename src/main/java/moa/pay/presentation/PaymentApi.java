package moa.pay.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.auth.Auth;
import moa.pay.client.dto.TossPaymentConfirmRequest;
import moa.pay.presentation.request.PrepayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "토스 결제 API", description = "토스 결제 관련 API")
public interface PaymentApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
            }
    )
    @Operation(summary = "결제 전 사전 결제정보 생성")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/prepay")
    ResponseEntity<Object> prepay(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody PrepayRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @Operation(summary = "결제 성공 후 처리")
    @GetMapping("/success")
    ResponseEntity<Void> paymentResult(
            @Valid @ModelAttribute TossPaymentConfirmRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @Operation(summary = "결제 실패 후 처리")
    @GetMapping("/fail")
    ResponseEntity<Void> paymentResult(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    );
}

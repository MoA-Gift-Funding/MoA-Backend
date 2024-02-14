package moa.pay;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.auth.Auth;
import moa.pay.request.PermitPaymentRequest;
import moa.pay.request.PrepayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "토스 결제 API", description = "토스 결제 관련 API")
public interface PaymentApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "결제 전 사전 결제정보 생성")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/prepay")
    ResponseEntity<Void> prepay(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody PrepayRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404")
            }
    )
    @Operation(summary = "결제 성공 후 최종 승인")
    @GetMapping("/success")
    ResponseEntity<Void> permitPayment(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @ModelAttribute PermitPaymentRequest request
    );
}

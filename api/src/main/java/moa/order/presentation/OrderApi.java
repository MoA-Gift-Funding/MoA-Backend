package moa.order.presentation;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.auth.Auth;
import moa.order.presentation.request.OrderPlaceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "주문 API", description = "주문 관련 API")
@SecurityRequirement(name = "JWT")
public interface OrderApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "500"),
            }
    )
    @Operation(summary = "주문 생성(상품 수령)")
    @PostMapping
    ResponseEntity<Void> place(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody OrderPlaceRequest request
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
    @Operation(summary = "쿠폰 재발행")
    ResponseEntity<Void> reissueCoupon(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Parameter(in = PATH, required = true, description = "주문 ID")
            @PathVariable("orderId") Long orderId
    );
}

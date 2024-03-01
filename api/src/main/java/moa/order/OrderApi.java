package moa.order;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.springframework.data.domain.Sort.Direction.DESC;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import moa.auth.Auth;
import moa.global.presentation.PageResponse;
import moa.order.query.response.OrderDetailResponse;
import moa.order.query.response.OrderResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "주문 API", description = "주문 관련 API")
@SecurityRequirement(name = "JWT")
public interface OrderApi {

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
    @Operation(summary = "쿠폰 취소")
    @PostMapping("/{orderId}/cancel-coupon")
    ResponseEntity<Void> cancelCoupon(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            
            @Parameter(in = PATH, required = true, description = "주문 ID")
            @PathVariable("orderId") Long orderId
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
    @Operation(summary = "내 주문 목록 조회")
    @GetMapping
    ResponseEntity<PageResponse<OrderResponse>> findOrders(
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
    @Operation(summary = "특정 주문 상세 조회")
    @GetMapping("/{orderId}")
    ResponseEntity<OrderDetailResponse> findOrderById(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "주문 ID")
            @PathVariable("orderId") Long orderId
    );
}

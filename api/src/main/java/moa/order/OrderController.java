package moa.order;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.springframework.data.domain.Sort.Direction.DESC;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.global.presentation.PageResponse;
import moa.order.application.OrderService;
import moa.order.query.OrderQueryService;
import moa.order.query.response.OrderDetailResponse;
import moa.order.query.response.OrderResponse;
import moa.order.request.ReissueCouponRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    // TODO 이거 윈큐브 상품(or 쿠폰형 상품에 특화된 로직이라 나중에 상품 종류 추가되면 구조 변경)
    // TODO 2차때 회의 후 구변
    // @PostMapping("/{orderId}/reissue-coupon")
    public ResponseEntity<Void> reissueCoupon(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody ReissueCouponRequest request
    ) {
        orderService.reissueCoupon(request.toCommand(memberId, orderId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> findOrders(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        Page<OrderResponse> result = orderQueryService.findMyOrders(memberId, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> findOrderById(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("orderId") Long orderId
    ) {
        OrderDetailResponse result = orderQueryService.findOrderById(memberId, orderId);
        return ResponseEntity.ok(result);
    }
}


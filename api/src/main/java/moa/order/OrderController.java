package moa.order;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.order.application.OrderService;
import moa.order.application.command.CouponReissueCommand;
import moa.order.request.OrderPlaceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> place(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody OrderPlaceRequest request
    ) {
        Long orderId = orderService.place(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    // TODO 이거 윈큐브 상품(or 쿠폰형 상품에 특화된 로직이라 나중에 상품 종류 추가되면 구조 변경)
    @PostMapping("/{orderId}/reissue-coupon")
    public ResponseEntity<Void> reissueCoupon(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("orderId") Long orderId
    ) {
        orderService.reissueCoupon(new CouponReissueCommand(memberId, orderId));
        return ResponseEntity.ok().build();
    }
}


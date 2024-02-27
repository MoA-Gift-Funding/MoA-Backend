package moa.order;


import lombok.RequiredArgsConstructor;
import moa.client.wincube.WincubeClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class DevOrderController {

    private final WincubeClient wincubeClient;

    @PostMapping("/orders/{orderId}")
    public void cancelCoupon(
            @PathVariable("orderId") Long orderId
    ) {
        wincubeClient.cancelCoupon(orderId);
    }
}

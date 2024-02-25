package moa.client.wincube;

import moa.client.wincube.dto.WincubeCancelCouponResponse;
import moa.client.wincube.dto.WincubeCheckCouponStatusResponse;
import moa.client.wincube.dto.WincubeIssueCouponResponse;
import moa.client.wincube.dto.WincubeProductResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface WincubeApiClient {

    /**
     * 상품 조회
     */
    @PostExchange("/salelist.do")
    WincubeProductResponse getProductList(
            @RequestParam("mdcode") String mdCode,  // 매체코드, 윈큐브에서 발급 후 전달
            @RequestParam("response_type") String responseType,  // JSON, XML(default)
            @RequestParam("token") String token
    );

    /**
     * 쿠폰 발행
     */
    @PostExchange("/request.do")
    WincubeIssueCouponResponse issueCoupon(
            @RequestParam("mdcode") String mdCode,  // 매체코드, 윈큐브에서 발급 후 전달
            @RequestParam("msg") String msg,  // MMS 전송할 메시지
            @RequestParam("title") String title,  // MMS 제목
            @RequestParam("callback") String callback,  // 발신자 번호
            @RequestParam("goods_id") String goodsId,  // 상품 아이디
            @RequestParam("phone_no") String phoneNo,  // 수신자 번호, (예: 01011112222)
            @RequestParam("tr_id") String trId,  // 우리가 부여한 고유번호, (예: gift_moa_00000000001)
            @RequestParam(value = "opt1", required = false) String opt1,  // 상품 옵션이 있을 경우 입력
            @RequestParam("response_type") String responseType,  // JSON, XML(default)
            @RequestParam("token") String token
    );

    /**
     * 발행 쿠폰 상태 확인
     */
    @PostExchange("/coupon_status.do")
    WincubeCheckCouponStatusResponse checkCouponStatus(
            @RequestParam("mdcode") String mdCode,  // 매체코드, 윈큐브에서 발급 후 전달
            @RequestParam("tr_id") String trId,  // 우리가 부여한 고유번호, (예: gift_moa_00000000001)
            @RequestParam("response_type") String responseType,  // JSON, XML(default)
            @RequestParam("token") String token
    );

    /**
     * 쿠폰 취소
     */
    @PostExchange("/coupon_cancel.do")
    WincubeCancelCouponResponse cancelCoupon(
            @RequestParam("mdcode") String mdCode,  // 매체코드, 윈큐브에서 발급 후 전달
            @RequestParam("tr_id") String trId,  // 우리가 부여한 고유번호, (예: gift_moa_00000000001)
            @RequestParam("response_type") String responseType,  // JSON, XML(default)
            @RequestParam("token") String token
    );
}

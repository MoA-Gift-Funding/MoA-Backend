package moa.product.client;

import moa.product.client.dto.WincubeProductResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface WincubeApiClient {

    @PostExchange("/salelist.do")
    WincubeProductResponse getProductList(
            @RequestParam("mdcode") String mdCode,  // 매체코드
            @RequestParam("response_type") String responseType,  // JSON, XML(default)
            @RequestParam("token") String token
    );
}

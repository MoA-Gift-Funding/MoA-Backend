package moa.product.client;

import moa.product.client.dto.WincubeAuthResponse;
import moa.product.client.dto.WincubeProductResponse;
import moa.product.client.dto.WincubeTokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface WincubeApiClient {

    @PostExchange("/salelist.do")
    WincubeProductResponse getProductList(
            @RequestParam("mdcode") String mdCode,  // 매체코드
            @RequestParam("response_type") String responseType,  // JSON, XML(default)
            @RequestParam("token") String token
    );

    @PostMapping("/auth/code/issue")
    WincubeAuthResponse getAuthToken(
            @RequestParam("custId") String custId, // 아이디 정보
            @RequestParam("pwd") String pwd,
            @RequestParam("autKey") String autKey,
            @RequestParam("aesKey") String aesKey,
            @RequestParam("aesIv") String aesIv
    );

    @PostMapping("/auth/token/check")
    WincubeTokenResponse checkToken(
            @RequestParam("tokenId") String token,
            @RequestParam("custId") String custId
    );

    @PostMapping("/auth/token/expire")
    WincubeTokenResponse expireToken(
            @RequestParam("tokenId") String token
    );
}

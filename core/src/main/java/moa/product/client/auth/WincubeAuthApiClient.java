package moa.product.client.auth;

import moa.product.client.dto.WincubeAuthResponse;
import moa.product.client.dto.WincubeTokenResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface WincubeAuthApiClient {

    @PostExchange("/auth/code/issue")
    WincubeAuthResponse getAuthToken(
            @RequestParam("custId") String custId, // 아이디 정보
            @RequestParam("pwd") String pwd,
            @RequestParam("autKey") String autKey,
            @RequestParam("aesKey") String aesKey,
            @RequestParam("aesIv") String aesIv
    );

    @PostExchange("/auth/token/check")
    WincubeTokenResponse checkToken(
            @RequestParam("tokenId") String token,
            @RequestParam("custId") String custId
    );

    @PostExchange("/auth/token/expire")
    WincubeTokenResponse expireToken(
            @RequestParam("tokenId") String token
    );
}

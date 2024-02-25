package moa.client.wincube.auth;

import moa.client.wincube.dto.WincubeIssueAuthCodeResponse;
import moa.client.wincube.dto.WincubeIssueAuthTokenResponse;
import moa.client.wincube.dto.WincubeTokenResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface WincubeAuthApiClient {

    /**
     * 계정 코드 발행
     * <p/>
     */
    @PostExchange("/auth/code/issue")
    WincubeIssueAuthCodeResponse issueAuthCode(
            @RequestParam("custId") String custId,  // 업체 AES256 암호화
            @RequestParam("pwd") String pwd,  // 업체 AES256 암호화
            @RequestParam("autKey") String autKey,  // 업체 AES256 암호화
            @RequestParam("aesKey") String aesKey,  // RSA 암호화
            @RequestParam("aesIv") String aesIv  // RSA 암호화
    );

    /**
     * 계정 토큰 발행
     * <p/>
     * codeId는 `계정 코드 발행`의 응답으로 받은 codeId 그대로 전송
     */
    @PostExchange("/auth/token/issue")
    WincubeIssueAuthTokenResponse issueAuthToken(
            @RequestParam("codeId") String codeId
    );

    /**
     * 계정 토큰 인증
     * <p/>
     * `계정 토큰 발행` 에서 발행된 토큰 정보(jwt)와 업체 아이디(custId, RSA 암호화 필요)를 전달한다.
     */
    @PostExchange("/auth/token/check")
    WincubeTokenResponse checkToken(
            @RequestParam("tokenId") String tokenId,
            @RequestParam("custId") String custId
    );

    @PostExchange("/auth/token/expire")
    WincubeTokenResponse expireToken(
            @RequestParam("tokenId") String token
    );
}

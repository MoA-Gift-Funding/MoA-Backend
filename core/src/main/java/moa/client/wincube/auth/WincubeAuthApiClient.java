package moa.client.wincube.auth;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import moa.client.wincube.auth.request.WincubeIssueAuthCodeRequest;
import moa.client.wincube.auth.request.WincubeIssueAuthTokenRequest;
import moa.client.wincube.dto.WincubeTokenResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface WincubeAuthApiClient {

    /**
     * 계정 코드 발행
     * TODO 이것도 예외 형식까지 응답 다 보고 변경
     */
    @PostExchange(value = "/auth/code/issue", contentType = APPLICATION_JSON_VALUE)
    String issueAuthCode(
            @RequestBody WincubeIssueAuthCodeRequest request
    );

    /**
     * 계정 토큰 발행
     * <p/>
     * codeId는 `계정 코드 발행`의 응답으로 받은 codeId 그대로 전송
     * <p/>
     * TODO 이것도 예외 형식까지 응답 다 보고 변경
     */
    @PostExchange("/auth/token/issue")
    String issueAuthToken(
            @RequestBody WincubeIssueAuthTokenRequest request
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

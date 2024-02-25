package moa.client.wincube.auth;

import static moa.product.exception.ProductExceptionType.PRODUCT_EXTERNAL_API_ERROR;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.jwt.JwtService;
import moa.product.client.dto.WincubeIssueAuthCodeResponse;
import moa.product.client.dto.WincubeIssueAuthTokenResponse;
import moa.product.client.dto.WincubeTokenSignature;
import moa.product.exception.ProductException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WincubeAuthClient {

    private final moa.product.client.auth.WincubeAuthProperty wincubeProperty;
    private final moa.product.client.auth.WincubeAuthApiClient wincubeAuthApiClient;
    private final moa.product.client.auth.Aes256 aes256;
    private final moa.product.client.auth.Rsa rsa;
    private final JwtService jwtService;

    // TODO 캐시
    public String getAuthToken() {
        String aesIv = generateAesIv();
        log.debug("AES IV 생성 완료");
        WincubeIssueAuthCodeResponse codeResponse = wincubeAuthApiClient.issueAuthCode(
                aes256.aes256Enc(wincubeProperty.custId(), aesIv),
                aes256.aes256Enc(wincubeProperty.pwd(), aesIv),
                aes256.aes256Enc(wincubeProperty.autKey(), aesIv),
                rsa.encode(wincubeProperty.aesKey()),
                rsa.encode(aesIv)
        );
        log.info("윈큐브 Auth Code 받아오기 완료");
        validateCodeResponse(codeResponse, aesIv);
        WincubeIssueAuthTokenResponse tokenResponse = wincubeAuthApiClient.issueAuthToken(codeResponse.codeId());
        log.info("윈큐브 Auth Token 받아오기 완료");
        validateTokenResponse(tokenResponse, aesIv);
        return tokenResponse.tokenId();
    }

    private String generateAesIv() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(8, 24);
    }

    private void validateCodeResponse(WincubeIssueAuthCodeResponse response, String aesIv) {
        if (response.resultCode() != 200) {
            log.error("Wincube AUTH CODE API ERROR {}", response);
            throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                    .withDetail(response.message())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
        String token = response.codeId();
        validateToken(token, aesIv);
    }

    private void validateTokenResponse(WincubeIssueAuthTokenResponse response, String aesIv) {
        if (response.resultCode() != 200) {
            log.error("Wincube AUTH TOKEN API ERROR {}", response);
            throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                    .withDetail(response.message())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
        String token = response.tokenId();
        validateToken(token, aesIv);
    }

    private void validateToken(String token, String aesIv) {
        WincubeTokenSignature sig = jwtService.decodePayload(token, WincubeTokenSignature.class);
        String decodedSig = aes256.aes256Denc(sig.signature(), wincubeProperty.aesKey(), aesIv);
        if (decodedSig.equals("wincube")) {
            log.error("윈큐브 Token 무결성 오류 발생");
            throw new ProductException(PRODUCT_EXTERNAL_API_ERROR.withDetail("윈큐브 Token 무결성 오류"));
        }
    }
}

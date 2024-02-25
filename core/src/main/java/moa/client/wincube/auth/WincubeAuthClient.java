package moa.client.wincube.auth;

import static moa.product.exception.ProductExceptionType.PRODUCT_EXTERNAL_API_ERROR;

import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.wincube.dto.WincubeIssueAuthCodeResponse;
import moa.client.wincube.dto.WincubeIssueAuthTokenResponse;
import moa.client.wincube.dto.WincubeTokenSignature;
import moa.global.jwt.JwtService;
import moa.product.exception.ProductException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WincubeAuthClient {

    private static final int AES_IV_BYTE = 16;

    private final WincubeAuthProperty wincubeProperty;
    private final WincubeAuthApiClient wincubeAuthApiClient;
    private final Aes256 aes256;
    private final Rsa rsa;
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
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[AES_IV_BYTE];
        secureRandom.nextBytes(key);
        return bytesToHex(key);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
        WincubeTokenSignature sig = jwtService.decodePayload(token,
                WincubeTokenSignature.class);
        String decodedSig = aes256.aes256Denc(sig.signature(), wincubeProperty.aesKey(), aesIv);
        if (decodedSig.equals("wincube")) {
            log.error("윈큐브 Token 무결성 오류 발생");
            throw new ProductException(PRODUCT_EXTERNAL_API_ERROR.withDetail("윈큐브 Token 무결성 오류"));
        }
    }
}

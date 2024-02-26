package moa.client.wincube.auth;

import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;
import static moa.client.wincube.auth.Aes256Iv.generateIv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.exception.ExternalApiException;
import moa.client.wincube.auth.request.WincubeIssueAuthCodeRequest;
import moa.client.wincube.auth.request.WincubeIssueAuthTokenRequest;
import moa.client.wincube.dto.WincubeIssueAuthCodeResponse;
import moa.client.wincube.dto.WincubeIssueAuthTokenResponse;
import moa.client.wincube.dto.WincubeResultCode;
import moa.client.wincube.dto.WincubeTokenSignature;
import moa.global.jwt.JwtService;
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
        String aesIv = generateIv(AES_IV_BYTE);
        String codeId = getAuthCode(aesIv);
        return getAuthToken(codeId, aesIv);
    }

    private String getAuthCode(String aesIv) {
        log.info("윈큐브 Auth Code 요청 호출");
        var response = wincubeAuthApiClient.issueAuthCode(
                new WincubeIssueAuthCodeRequest(
                        aes256.aes256Enc(wincubeProperty.custId(), aesIv),
                        aes256.aes256Enc(wincubeProperty.pwd(), aesIv),
                        aes256.aes256Enc(wincubeProperty.autKey(), aesIv),
                        rsa.encode(wincubeProperty.aesKey()),
                        rsa.encode(aesIv))
        );
        validateResponseIsSuccess(response);
        log.info("윈큐브 Auth Code 받아오기 완료: {}", response);
        validateCodeResponse(response, aesIv);
        return response.codeId();
    }

    private void validateResponseIsSuccess(WincubeResultCode response) {
        log.info("윈큐브 Auth Code 응답 검증");
        if (response.resultCode() >= 400) {
            log.error("Wincube AUTH API error {}", response);
            throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                    .withDetail(response.toString())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
    }

    private void validateCodeResponse(WincubeIssueAuthCodeResponse response, String aesIv) {
        String token = response.codeId();
        validateToken(token, aesIv);
    }

    private void validateToken(String token, String aesIv) {
        WincubeTokenSignature sig = jwtService.decodePayload(token, WincubeTokenSignature.class);
        String decodedSig = aes256.aes256Denc(sig.signature(), wincubeProperty.aesKey(), aesIv);
        if (!decodedSig.equals("wincube")) {
            log.error("윈큐브 Token 무결성 오류 발생");
            throw new ExternalApiException(EXTERNAL_API_EXCEPTION.withDetail("윈큐브 Token 무결성 오류"));
        }
    }

    private String getAuthToken(String codeId, String aesIv) {
        var response = wincubeAuthApiClient.issueAuthToken(new WincubeIssueAuthTokenRequest(codeId));
        validateResponseIsSuccess(response);
        log.info("윈큐브 Auth Token 받아오기 완료: {}", response);
        validateTokenResponse(response, aesIv);
        return response.tokenId();
    }

    private void validateTokenResponse(WincubeIssueAuthTokenResponse response, String aesIv) {
        if (response.resultCode() != 200) {
            log.error("Wincube AUTH TOKEN API ERROR {}", response);
            throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                    .withDetail(response.message())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
        String token = response.tokenId();
        validateToken(token, aesIv);
    }
}

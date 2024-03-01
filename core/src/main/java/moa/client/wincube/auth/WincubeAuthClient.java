package moa.client.wincube.auth;

import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;
import static moa.client.wincube.auth.Aes256Iv.generateIv;
import static moa.global.config.cache.CacheConfig.WINCUBE_ACCESS_TOKEN_CACHE_MANAGER_NAME;
import static moa.global.config.cache.CacheConfig.WINCUBE_ACCESS_TOKEN_CACHE_NAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.discord.DiscordWebHookClient;
import moa.client.exception.ExternalApiException;
import moa.client.wincube.auth.request.WincubeAuthResultCode;
import moa.client.wincube.auth.request.WincubeIssueAuthCodeRequest;
import moa.client.wincube.auth.request.WincubeIssueAuthTokenRequest;
import moa.client.wincube.dto.WincubeIssueAuthCodeResponse;
import moa.client.wincube.dto.WincubeIssueAuthTokenResponse;
import moa.client.wincube.dto.WincubeTokenSignature;
import moa.global.jwt.JwtService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WincubeAuthClient {

    private static final int AES_IV_BYTE = 16;

    private final DiscordWebHookClient discordWebHookClient;
    private final ObjectMapper objectMapper;
    private final WincubeAuthProperty wincubeProperty;
    private final WincubeAuthApiClient wincubeAuthApiClient;
    private final Aes256 aes256;
    private final Rsa rsa;
    private final JwtService jwtService;

    @Cacheable(
            cacheNames = WINCUBE_ACCESS_TOKEN_CACHE_NAME,
            cacheManager = WINCUBE_ACCESS_TOKEN_CACHE_MANAGER_NAME
    )
    public String getAuthToken() {
        String aesIv = generateIv(AES_IV_BYTE);
        String codeId = getAuthCode(aesIv);
        return getAuthToken(codeId, aesIv);
    }

    private String getAuthCode(String aesIv) {
        log.info("윈큐브 Auth Code 요청 호출");
        String response = wincubeAuthApiClient.issueAuthCode(
                new WincubeIssueAuthCodeRequest(
                        aes256.aes256Enc(wincubeProperty.custId(), aesIv),
                        aes256.aes256Enc(wincubeProperty.pwd(), aesIv),
                        aes256.aes256Enc(wincubeProperty.autKey(), aesIv),
                        rsa.encode(wincubeProperty.aesKey()),
                        rsa.encode(aesIv))
        );
        validateResponseIsSuccess(response);
        var codeResponse = readValue(
                response,
                WincubeIssueAuthCodeResponse.class
        );
        log.info("윈큐브 Auth Code 받아오기 완료: {}", codeResponse);
        validateCodeResponse(codeResponse, aesIv);
        return codeResponse.codeId();
    }

    private void validateResponseIsSuccess(String response) {
        log.info("윈큐브 Auth Code 응답 검증");
        var code = readValue(response, WincubeAuthResultCode.class);
        if (code.resultCode() >= 400) {
            log.error("Wincube AUTH CODE API ERROR {}", response);
            discordWebHookClient.sendToErrorChannel("Wincube AUTH CODE API ERROR \n ->" + response);
            throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                    .withDetail(response)
                    .setStatus(HttpStatus.valueOf(code.resultCode())));
        }
        log.info("Wincube AUTH API response {}", code);
    }

    private <T> T readValue(String data, Class<T> type) {
        try {
            return objectMapper.readValue(data, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
        String response = wincubeAuthApiClient.issueAuthToken(new WincubeIssueAuthTokenRequest(codeId));
        validateResponseIsSuccess(response);
        var token = readValue(response, WincubeIssueAuthTokenResponse.class);
        log.info("윈큐브 Auth Token 받아오기 완료: {}", token);
        validateTokenResponse(token, aesIv);
        return token.tokenId();
    }

    private void validateTokenResponse(WincubeIssueAuthTokenResponse response, String aesIv) {
        if (response.resultCode() != 200) {
            log.error("Wincube AUTH TOKEN API ERROR {}", response);
            discordWebHookClient.sendToErrorChannel("Wincube AUTH TOKEN API ERROR \n ->" + response);
            throw new ExternalApiException(EXTERNAL_API_EXCEPTION
                    .withDetail(response.message())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
        String token = response.tokenId();
        validateToken(token, aesIv);
    }
}

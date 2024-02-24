package moa.product.client.auth;

import static moa.product.exception.ProductExceptionType.PRODUCT_EXTERNAL_API_ERROR;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.product.client.dto.WincubeIssueAuthCodeResponse;
import moa.product.client.dto.WincubeIssueAuthTokenResponse;
import moa.product.exception.ProductException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WincubeAuthClient {

    private final WincubeAuthProperty wincubeProperty;
    private final WincubeAuthApiClient wincubeAuthApiClient;
    private final Aes256 aes256;
    private final Rsa rsa;

    public String getAuthToken() {
        String aesIv = generateAesIv();
        WincubeIssueAuthCodeResponse codeResponse = wincubeAuthApiClient.issueAuthCode(
                aes256.aes256Enc(wincubeProperty.custId(), aesIv),
                aes256.aes256Enc(wincubeProperty.pwd(), aesIv),
                aes256.aes256Enc(wincubeProperty.autKey(), aesIv),
                rsa.encode(wincubeProperty.aesKey()),
                rsa.encode(aesIv)
        );
        validateResponse(codeResponse);
        WincubeIssueAuthTokenResponse tokenResponse = wincubeAuthApiClient.issueAuthToken(codeResponse.codeId());
        validateResponse(tokenResponse);
        return tokenResponse.tokenId();
    }

    private void validateResponse(WincubeIssueAuthCodeResponse response) {
        if (response.resultCode() != 200) {
            log.error("Wincube AUTH CODE API ERROR {}", response);
            throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                    .withDetail(response.message())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
    }

    private void validateResponse(WincubeIssueAuthTokenResponse response) {
        if (response.resultCode() != 200) {
            log.error("Wincube AUTH TOKEN API ERROR {}", response);
            throw new ProductException(PRODUCT_EXTERNAL_API_ERROR
                    .withDetail(response.message())
                    .setStatus(HttpStatus.valueOf(response.resultCode())));
        }
    }

    private String generateAesIv() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(8, 24);
    }
}

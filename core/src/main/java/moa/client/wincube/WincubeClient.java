package moa.client.wincube;

import static moa.product.exception.ProductExceptionType.COUPONS_CANNOT_BE_REISSUED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.wincube.auth.WincubeAuthClient;
import moa.client.wincube.dto.WincubeCancelCouponResponse;
import moa.client.wincube.dto.WincubeCheckCouponStatusResponse;
import moa.client.wincube.dto.WincubeIssueCouponResponse;
import moa.client.wincube.dto.WincubeProductResponse;
import moa.product.exception.ProductException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WincubeClient {

    private static final String TR_ID_PREFIX = "giftmoa_";
    private static final String JSON = "JSON";

    private final ObjectMapper objectMapper;
    private final WincubeProperty wincubeProperty;
    private final WincubeAuthClient authClient;
    private final WincubeApiClient client;

    public WincubeProductResponse getProductList() {
        String authToken = authClient.getAuthToken();
        String productList = client.getProductList(wincubeProperty.mdCode(), JSON, authToken);
        log.info("윈큐브 상품 정보 조회 완료: {}", productList);
        return readValue(productList, WincubeProductResponse.class);
    }

    private <T> T readValue(String data, Class<T> type) {
        try {
            return objectMapper.readValue(data, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void issueCoupon(
            Long orderId,
            String title,
            String message,
            String productId,
            String phoneNumber,
            @Nullable String optionId
    ) {
        String authToken = authClient.getAuthToken();
        WincubeIssueCouponResponse response = client.issueCoupon(
                wincubeProperty.mdCode(),
                message,
                title,
                wincubeProperty.callback(),
                productId,
                phoneNumber,
                TR_ID_PREFIX + orderId,
                optionId,
                JSON,
                authToken
        );
        loggingIssueCoupon(response);
    }

    // TODO 2차때 회의 후, trId 어케할지 결정하고 처리
    public void reissueCoupon(
            Long orderId,
            String title,
            String message,
            String productId,
            String phoneNumber,
            @Nullable String optionId
    ) {
        String authToken = authClient.getAuthToken();
        WincubeCheckCouponStatusResponse status = client.checkCouponStatus(
                wincubeProperty.mdCode(),
                TR_ID_PREFIX + orderId,
                JSON,
                authToken
        );
        validateCancellable(status);
        WincubeCancelCouponResponse cancelResponse = client.cancelCoupon(
                wincubeProperty.mdCode(),
                TR_ID_PREFIX + orderId,
                JSON,
                authToken
        );
        validateCancelSuccess(cancelResponse);
        WincubeIssueCouponResponse issueResponse = client.issueCoupon(
                wincubeProperty.mdCode(),
                message,
                title,
                wincubeProperty.callback(),
                productId,
                phoneNumber,
                TR_ID_PREFIX + orderId,
                optionId,
                JSON,
                authToken
        );
        loggingIssueCoupon(issueResponse);
    }

    private void validateCancellable(WincubeCheckCouponStatusResponse response) {
        if (!response.cancellable()) {
            log.info("쿠폰 취소 불가 {}", response);
            throw new ProductException(COUPONS_CANNOT_BE_REISSUED
                    .withDetail(response.result().statusCode() + ", " + response.result().statusText())
            );
        }
    }

    private void validateCancelSuccess(WincubeCancelCouponResponse response) {
        if (!response.isSuccess()) {
            log.info("쿠폰 취소 API 에러 {}", response);
            throw new ProductException(COUPONS_CANNOT_BE_REISSUED
                    .withDetail(response.result().statusCode() + ", " + response.result().statusText())
            );
        }
    }

    private void loggingIssueCoupon(WincubeIssueCouponResponse response) {
        if (response.isSuccess()) {
            log.info("윈큐브 쿠폰 발행 완료 {}", response);
        } else {
            log.error("윈큐브 쿠폰 발행 실패 {}", response);
        }
    }
}

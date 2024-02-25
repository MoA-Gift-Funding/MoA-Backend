package moa.client.wincube;

import static moa.product.exception.ProductExceptionType.COUPONS_CANNOT_BE_REISSUED;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.order.domain.OrderTransactionId;
import moa.product.client.auth.WincubeAuthClient;
import moa.product.client.dto.WincubeCancelCouponResponse;
import moa.product.client.dto.WincubeCheckCouponStatusResponse;
import moa.product.client.dto.WincubeIssueCouponResponse;
import moa.product.client.dto.WincubeProductResponse;
import moa.product.exception.ProductException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WincubeClient {

    private static final String JSON = "JSON";

    private final WincubeProperty wincubeProperty;
    private final WincubeAuthClient authClient;
    private final WincubeApiClient client;

    public WincubeProductResponse getProductList() {
        String authToken = authClient.getAuthToken();
        return client.getProductList(wincubeProperty.mdCode(), JSON, authToken);
    }

    public void issueCoupon(
            OrderTransactionId orderTransactionId,
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
                orderTransactionId.id(),
                optionId,
                JSON,
                authToken
        );
        loggingIssueCoupon(response);
    }

    public void reissueCoupon(
            OrderTransactionId orderTransactionId,
            String title,
            String message,
            String productId,
            String phoneNumber,
            @Nullable String optionId
    ) {
        String authToken = authClient.getAuthToken();
        WincubeCheckCouponStatusResponse status = client.checkCouponStatus(
                wincubeProperty.mdCode(),
                orderTransactionId.id(),
                JSON,
                authToken
        );
        validateCancellable(status);
        WincubeCancelCouponResponse cancelResponse = client.cancelCoupon(
                wincubeProperty.mdCode(),
                orderTransactionId.id(),
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
                orderTransactionId.id(),
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

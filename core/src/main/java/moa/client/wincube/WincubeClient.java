package moa.client.wincube;

import static moa.client.exception.ExternalApiExceptionType.EXTERNAL_API_EXCEPTION;
import static moa.product.exception.ProductExceptionType.COUPONS_CANNOT_BE_REISSUED;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.exception.ExternalApiException;
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
        WincubeProductResponse productList = client.getProductList(wincubeProperty.mdCode(), JSON, authToken);
        log.info("윈큐브 상품 정보 조회 완료 [code: {}, 상품 개수: {}]", productList.resultCode(), productList.goodsNum());
        return productList;
    }

    public void issueCoupon(
            String transactionId,
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
                TR_ID_PREFIX + transactionId,
                optionId,
                JSON,
                authToken
        );
        log.info("윈큐브 쿠폰 발행 API 호출 완료.\n -> 응답: {}", response);
        validateIssueCoupon(response);
    }

    private void validateIssueCoupon(WincubeIssueCouponResponse response) {
        if (response.isSuccess()) {
            log.info("윈큐브 쿠폰 발행 완료 {}", response);
        } else {
            throw new ExternalApiException(EXTERNAL_API_EXCEPTION.withDetail("윈큐브 쿠폰 발행 실패: " + response));
        }
    }

    public void cancelCoupon(Long orderId) {
        String authToken = authClient.getAuthToken();
        WincubeCancelCouponResponse response = client.cancelCoupon(
                wincubeProperty.mdCode(),
                TR_ID_PREFIX + orderId,
                JSON,
                authToken
        );
        log.info("윈큐브 쿠폰 취소 API 호출 완료.\n -> 응답: {}", response);
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
//        String authToken = authClient.getAuthToken();
//        WincubeCheckCouponStatusResponse status = client.checkCouponStatus(
//                wincubeProperty.mdCode(),
//                TR_ID_PREFIX + orderId,
//                JSON,
//                authToken
//        );
//        validateCancellable(status);
//        WincubeCancelCouponResponse cancelResponse = client.cancelCoupon(
//                wincubeProperty.mdCode(),
//                TR_ID_PREFIX + orderId,
//                JSON,
//                authToken
//        );
//        validateCancelSuccess(cancelResponse);
//        WincubeIssueCouponResponse issueResponse = client.issueCoupon(
//                wincubeProperty.mdCode(),
//                message,
//                title,
//                wincubeProperty.callback(),
//                productId,
//                phoneNumber,
//                TR_ID_PREFIX + orderId,
//                optionId,
//                JSON,
//                authToken
//        );
//        loggingIssueCoupon(issueResponse);
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
                    .withDetail(response.statusCode() + ", " + response.statusText())
            );
        }
    }
}

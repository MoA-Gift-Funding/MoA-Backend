package moa.product.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WincubeCheckCouponStatusResponse(
        Result result
) {
    public boolean cancellable() {
        return result.statusCode.equals("0");
    }

    public record Result(
            @JsonProperty("StatusCode") String statusCode,  // 취소가능이면 0, 그 외 실패
            @JsonProperty("StatusText") String statusText  // 실패사유
    ) {
    }
}

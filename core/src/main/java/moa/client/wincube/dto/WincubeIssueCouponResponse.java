package moa.client.wincube.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = SnakeCaseStrategy.class)
public record WincubeIssueCouponResponse(
        Result result,
        Value value
) {
    public static final String SUCCESS_CODE = "0";

    public boolean isSuccess() {
        return result.code.equals(SUCCESS_CODE);
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record Result(
            String code,
            String reason
    ) {
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record Value(
            String mdn,  // 전화번호
            String trId,  // 우리가 부여한 고유반호
            String ctrId, // 기프팅 거래번호
            String createDateTime  // 쿠폰 생성시간
    ) {
    }
}

package moa.client.wincube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = SnakeCaseStrategy.class)
public record WincubeCancelCouponResponse(
        @JsonProperty("trID") String trId,
        @JsonProperty("StatusCode") String statusCode,
        @JsonProperty("StatusText") String statusText,
        @JsonProperty("cancelDateTime") String cancelDateTime
) {
    public static final String SUCCESS_CODE = "0";

    public boolean isSuccess() {
        return statusCode.equals(SUCCESS_CODE);
    }
}

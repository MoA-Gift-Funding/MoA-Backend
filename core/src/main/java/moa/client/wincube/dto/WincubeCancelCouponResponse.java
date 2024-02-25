package moa.client.wincube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = SnakeCaseStrategy.class)
public record WincubeCancelCouponResponse(
        Result result
) {
    public static final String SUCCESS_CODE = "0";

    public boolean isSuccess() {
        return result.statusCode.equals(SUCCESS_CODE);
    }

    public record Result(
            @JsonProperty("trID") String trId,
            @JsonProperty("StatusCode") String statusCode,
            @JsonProperty("StatusText") String statusText,
            @JsonProperty("cancelDateTime") String cancelDateTime
    ) {
    }
}

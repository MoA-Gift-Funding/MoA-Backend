package moa.funding.presentation.request;

import static moa.funding.domain.Visibility.PUBLIC;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import moa.funding.application.command.FundingCreateCommand;
import moa.global.domain.Price;
import org.springframework.format.annotation.DateTimeFormat;

public record FundingCreateRequest(
        @Schema(example = "1")
        @NotNull Long productId,

        @Schema(example = "주노에게 주는 소박한 선물")
        @NotBlank String title,

        @Schema(example = "에어팟 맥스를 원합니다!!!")
        @NotBlank String description,

        @Schema(description = "펀딩 종료일 (한 달을 초과해서는 안된다.)", example = "2023-12-25")
        @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,

        @Schema(description = "최대 펀딩 가능 금액", example = "5500")
        @NotBlank String maximumAmount,

        @Schema(description = "배송지 ID", example = "1")
        Long deliveryAddressId,

        @Schema(description = "배송시 요청사항", example = "택배함 옆에 놔주세요")
        String deliveryRequestMessage
) {
    public FundingCreateCommand toCommand(Long memberId) {
        return new FundingCreateCommand(
                memberId,
                title,
                description,
                LocalDate.parse(endDate),
                PUBLIC,
                Price.from(maximumAmount),
                productId,
                deliveryAddressId,
                deliveryRequestMessage
        );
    }
}

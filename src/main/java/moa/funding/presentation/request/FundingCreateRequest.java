package moa.funding.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Address;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.Price;
import moa.funding.domain.Visibility;
import org.springframework.format.annotation.DateTimeFormat;

public record FundingCreateRequest(
        @Schema(example = "1") @NotNull Long productId,
        @Schema(example = "주노에게 주는 소박한 선물") @NotBlank String title,
        @Schema(example = "에어팟 맥스를 원합니다!!!") @NotBlank String description,
        @Schema(example = "2023-12-25") @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
        @Schema(description = "최대 펀딩 가능 금액. 0이면 무제한", example = "5500") @NotBlank String maximumPrice,

        // 배송 정보
        // 이름, 핸드폰 번호
        @Schema(description = "", example = "주노") @NotBlank String userName,
        @Schema(description = "", example = "010-1234-5678") @NotBlank String phoneNumber,

        // 상세 주소정보
        @Schema(description = "우편번호", example = "13529") String zonecode,
        @Schema(description = "도로명 주소", example = "경기 성남시 분당구 판교역로 166 (카카오 판교 아지트)") String roadAddress,
        @Schema(description = "지번 주소", example = "경기 성남시 분당구 백현동 532") String jibunAddress,
        @Schema(description = "상세주소", example = "판교 아지트 3층 택배함") String detailAddress,
        @Schema(description = "배송 문의", example = "택배함 옆에 놔주세요") String message
) {

    public FundingCreateCommand toCommand(Long memberId) {
        return new FundingCreateCommand(
                memberId,
                productId,
                title,
                description,
                LocalDate.parse(endDate),
                new Price(maximumPrice),
                new Address(
                        zonecode, roadAddress, jibunAddress, detailAddress
                ),
                Visibility.PUBLIC,
                FundingStatus.PREPARING
        );
    }
}

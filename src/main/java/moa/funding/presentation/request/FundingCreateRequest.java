package moa.funding.presentation.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Address;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.Visibility;

public record FundingCreateRequest(
    @Schema(example = "PRODUCT_ID_148EX3y") @NotBlank String productId,
    @Schema(example = "주노에게 주는 소박한 선물") @NotBlank String title,
    @Schema(example = "에어팟 맥스를 원합니다!!!") @NotBlank String description,
    @Schema(example = "2023-12-25") @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
    @Schema(example = "5500") @NotBlank String maximumPrice,
    @Schema(example = "5000") @NotBlank String minimumPrice,

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
            new BigDecimal(maximumPrice),
            new BigDecimal(minimumPrice),
            new Address(
                zonecode, roadAddress, jibunAddress, detailAddress
            ),
            Visibility.ALL,
            FundingStatus.PROCESSING
        );
    }
}

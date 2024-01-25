package moa.address.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.address.application.command.AddressUpdateCommand;

public record AddressUpdateRequest(
        @Schema(description = "배송지명", example = "여의도집")
        @NotBlank String name,

        @Schema(description = "받는 사람 이름", example = "주노")
        @NotBlank String recipientName,

        @Schema(description = "받는 사람 전화번호", example = "010-1234-5678")
        @NotBlank String phoneNumber,

        @Schema(description = "우편번호", example = "13529")
        String zonecode,

        @Schema(description = "도로명 주소", example = "경기 성남시 분당구 판교역로 166 (카카오 판교 아지트)")
        String roadAddress,

        @Schema(description = "지번 주소", example = "경기 성남시 분당구 백현동 532")
        String jibunAddress,

        @Schema(description = "상세주소", example = "판교 아지트 3층 택배함")
        String detailAddress,

        @Schema(description = "기본 주소지로 설정할지 여부")
        boolean isDefault
) {
    public AddressUpdateCommand toCommand(Long memberId, Long deliveryAddressId) {
        return new AddressUpdateCommand(
                memberId,
                deliveryAddressId,
                name,
                recipientName,
                phoneNumber,
                zonecode,
                roadAddress,
                jibunAddress,
                detailAddress,
                isDefault
        );
    }
}

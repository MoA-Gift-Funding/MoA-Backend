package moa.product.client.dto;

public record WincubeIssueAuthCodeResponse(
        int resultCode,
        String codeId,
        String expireDate,
        String expireTime,
        String message
) {
}

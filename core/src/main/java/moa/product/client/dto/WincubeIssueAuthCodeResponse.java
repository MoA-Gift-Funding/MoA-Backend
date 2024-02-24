package moa.product.client.dto;

public record WincubeIssueAuthCodeResponse(
        Integer resultCode,
        String codeId,
        String expireDate,
        String expireTime,
        String message
) {
}

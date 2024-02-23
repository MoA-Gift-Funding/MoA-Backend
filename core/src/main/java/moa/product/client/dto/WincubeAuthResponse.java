package moa.product.client.dto;

public record WincubeAuthResponse(
        Integer resultCode,
        String codeId,
        String expireDate,
        String expireTime,
        String message
) {
}

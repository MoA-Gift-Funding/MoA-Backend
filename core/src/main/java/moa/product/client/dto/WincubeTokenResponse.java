package moa.product.client.dto;

public record WincubeTokenResponse(
        Integer resultCode,
        String status,
        String expireDate,
        String expireTime,
        String message
) {
}

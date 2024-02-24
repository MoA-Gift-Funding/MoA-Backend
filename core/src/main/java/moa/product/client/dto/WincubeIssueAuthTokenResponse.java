package moa.product.client.dto;

public record WincubeIssueAuthTokenResponse(
        Integer resultCode,
        String tokenId,
        String expireDate,
        String expireTime,
        String message
) {
}

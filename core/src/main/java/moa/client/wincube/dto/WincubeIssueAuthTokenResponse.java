package moa.client.wincube.dto;

public record WincubeIssueAuthTokenResponse(
        Integer resultCode,
        String tokenId,
        String expireDate,
        String expireTime,
        String message
) {
}

package moa.client.wincube.dto;

public record WincubeIssueAuthTokenResponse(
        int resultCode,
        String message,
        String tokenId,
        String expireDate,
        String expireTime
) implements WincubeResultCode {
}

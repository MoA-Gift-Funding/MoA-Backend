package moa.client.wincube.dto;

public record WincubeIssueAuthCodeResponse(
        int resultCode,
        String codeId,
        String expireDate,
        String expireTime,
        String message
) {
}

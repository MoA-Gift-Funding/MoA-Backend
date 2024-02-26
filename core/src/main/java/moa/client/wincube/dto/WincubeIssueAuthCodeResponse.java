package moa.client.wincube.dto;

public record WincubeIssueAuthCodeResponse(
        int resultCode,
        String message,
        String codeId,
        String expireDate,
        String expireTime
) implements WincubeResultCode {
}

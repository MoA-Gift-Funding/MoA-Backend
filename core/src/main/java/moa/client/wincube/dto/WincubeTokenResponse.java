package moa.client.wincube.dto;

public record WincubeTokenResponse(
        Integer resultCode,
        String status,  // 값이 A 이면 사용 가능 토큰
        String expireDate,
        String expireTime,
        String message
) {
}

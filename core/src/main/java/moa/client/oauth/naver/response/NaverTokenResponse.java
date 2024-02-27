package moa.client.oauth.naver.response;

public record NaverTokenResponse(
        String accessToken,
        String tokenType,
        String expiresIn,
        String refreshToken
) {
}

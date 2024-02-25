package moa.client.oauth.apple.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = SnakeCaseStrategy.class)
public record AppleTokenResponse(
        String accessToken,
        String expiresIn,
        String idToken,
        String refreshToken,
        String tokenType,
        String error
) {
}

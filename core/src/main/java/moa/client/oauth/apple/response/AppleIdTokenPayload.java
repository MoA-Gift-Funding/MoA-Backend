package moa.client.oauth.apple.response;

public record AppleIdTokenPayload(
        String sub,
        String email
) {
}

package moa.client.oauth.naver.response;

public record NaverWithdrawResponse(
        String accessToken,
        String result
) {
}

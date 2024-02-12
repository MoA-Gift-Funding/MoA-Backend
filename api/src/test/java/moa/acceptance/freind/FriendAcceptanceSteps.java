package moa.acceptance.freind;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import moa.acceptance.AcceptanceSupport;
import moa.friend.request.SyncContactRequest;

@SuppressWarnings("NonAsciiCharacters")
public class FriendAcceptanceSteps {

    public static ExtractableResponse<Response> 연락처_동기화(String 요청자_token, SyncContactRequest request) {
        return AcceptanceSupport.given(요청자_token)
                .body(request)
                .post("/friends/sync-contact")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 친구_차단_요청(String 요청자_token, Long 대상_친구_ID) {
        return AcceptanceSupport.given(요청자_token)
                .post("/friends/block/{id}", 대상_친구_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 친구_차단_해제_요청(String 요청자_token, Long 대상_친구_ID) {
        return AcceptanceSupport.given(요청자_token)
                .post("/friends/unblock/{id}", 대상_친구_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_친구_목록_조회_요청(String accessToken) {
        return AcceptanceSupport.given(accessToken)
                .get("/friends")
                .then()
                .extract();
    }
}

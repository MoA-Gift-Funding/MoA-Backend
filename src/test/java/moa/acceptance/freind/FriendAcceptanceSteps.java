package moa.acceptance.freind;

import static moa.acceptance.AcceptanceSupport.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import moa.friend.presentation.request.SyncContactRequest;

@SuppressWarnings("NonAsciiCharacters")
public class FriendAcceptanceSteps {

    public static final boolean 차단됨 = true;
    public static final boolean 차단되지_않음 = false;

    public static ExtractableResponse<Response> 연락처_동기화(String 말랑_Token, SyncContactRequest request) {
        return given(말랑_Token)
                .body(request)
                .post("/friends/sync-contact")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 친구_차단_요청(String 말랑_Token, Long 나쁜놈_친구_ID) {
        return given(말랑_Token)
                .post("/friends/block/{id}", 나쁜놈_친구_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 친구_차단_해제_요청(String 말랑_Token, Long 나쁜놈_친구_ID) {
        return given(말랑_Token)
                .post("/friends/unblock/{id}", 나쁜놈_친구_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_친구_목록_조회_요청(String accessToken, boolean 차단여부) {
        return given(accessToken)
                .queryParam("isBlocked", 차단여부)
                .get("/friends/my")
                .then()
                .extract();
    }
}

package moa.acceptance.funding;

import static moa.acceptance.AcceptanceSupport.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class FundingAcceptanceSteps {

    public static ExtractableResponse<Response> 펀딩_생성_요청(String 준호_Token, Object request) {
        return given(준호_Token)
                .body(request)
                .post("/fundings")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 나의_펀딩목록_조회_요청(String 준호_Token, Object request) {
        return given(준호_Token)
                .body(request)
                .get("/fundings/my")
                .then()
                .extract();
    }
}

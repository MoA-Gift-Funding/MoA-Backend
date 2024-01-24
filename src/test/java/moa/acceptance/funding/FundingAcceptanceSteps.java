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

    public static Long 펀딩_생성_요청_ID_반환(String 준호_Token, Object request) {
        var response = 펀딩_생성_요청(준호_Token, request);
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    public static ExtractableResponse<Response> 나의_펀딩목록_조회_요청(String 준호_Token) {
        return given(준호_Token)
                .get("/fundings/my")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 펀딩_상세_조회_요청(String 준호_Token, Long fundingId) {
        return given(준호_Token)
                .get("/fundings/{fundingId}", fundingId)
                .then()
                .extract();
    }
}

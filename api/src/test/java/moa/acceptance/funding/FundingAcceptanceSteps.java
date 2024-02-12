package moa.acceptance.funding;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import moa.acceptance.AcceptanceSupport;

@SuppressWarnings("NonAsciiCharacters")
public class FundingAcceptanceSteps {

    public static ExtractableResponse<Response> 펀딩_생성_요청(String 회원_토큰, Object request) {
        return AcceptanceSupport.given(회원_토큰)
                .body(request)
                .post("/fundings")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 나의_펀딩목록_조회_요청(String 회원_토큰) {
        return AcceptanceSupport.given(회원_토큰)
                .get("/fundings/my")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 펀딩_상세_조회_요청(String 회원_토큰, Long fundingId) {
        return AcceptanceSupport.given(회원_토큰)
                .get("/fundings/{fundingId}", fundingId)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 펀딩_목록_조회_요청(String 회원_토큰) {
        return AcceptanceSupport.given(회원_토큰)
                .get("/fundings")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 펀딩_참여_요청(String 회원_토큰, Long fundingId, Object request) {
        return AcceptanceSupport.given(회원_토큰)
                .body(request)
                .post("/fundings/{fundingId}/participate", fundingId)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 펀딩_메시지_조회_요청(String 회원_토큰) {
        return AcceptanceSupport.given(회원_토큰)
                .get("/fundings/messages")
                .then()
                .extract();
    }
}

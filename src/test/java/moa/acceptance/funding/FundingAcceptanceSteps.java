package moa.acceptance.funding;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import static moa.acceptance.AcceptanceSupport.given;

@SuppressWarnings("NonAsciiCharacters")
public class FundingAcceptanceSteps {

    public static ExtractableResponse<Response> 펀딩_생성_요청(String 준호_Token, Object request) {
        return given(준호_Token)
            .body(request)
            .post("/fundings")
            .then()
            .extract();
    }
}

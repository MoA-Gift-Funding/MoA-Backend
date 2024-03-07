package moa.acceptance.member;

import static moa.acceptance.AcceptanceSupport.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import moa.client.oauth.kakao.response.KakaoMemberResponse;
import moa.client.oauth.kakao.response.KakaoMemberResponse.KakaoAccount;
import moa.client.oauth.kakao.response.KakaoMemberResponse.Profile;
import moa.global.jwt.JwtResponse;
import moa.member.query.response.MemberResponse;
import moa.member.query.response.NotificationStatusResponse;
import moa.member.request.MemberUpdateRequest;
import moa.member.request.NotificationPermitRequest;
import moa.member.request.SendPhoneVerificationNumberRequest;
import moa.member.request.SignupRequest;
import moa.member.request.VerifyPhoneRequest;

public class MemberAcceptanceSteps {

    public static KakaoMemberResponse 카카오톡_회원_조회_응답(
            Long id,
            String 닉네임,
            String 프로필_URL,
            String email,
            String birthyear,
            String birthday,
            String phone
    ) {
        return new KakaoMemberResponse(
                id,
                false,
                null,
                new KakaoAccount(
                        true,
                        true,
                        true,
                        new Profile(
                                닉네임,
                                null,
                                프로필_URL,
                                false
                        ),
                        true,
                        null,
                        true,
                        true,
                        true,
                        email,
                        true,
                        "20",
                        true,
                        birthyear,
                        true,
                        birthday,
                        null,
                        true,
                        null,
                        true,
                        phone,
                        false,
                        null,
                        null
                )
        );
    }

    public static String 카카오톡_로그인(String accessToken) {
        return given()
                .header("OAuthAccessToken", accessToken)
                .get("/oauth/login/app/KAKAO")
                .then()
                .extract()
                .as(JwtResponse.class)
                .accessToken();
    }

    public static boolean 이메일_중복_체크(String accessToken, String email) {
        return given(accessToken)
                .queryParam("email", email)
                .get("/members/email-check")
                .then()
                .extract()
                .as(Boolean.class);
    }

    public static ExtractableResponse<Response> 핸드폰_인증번호_전송_요청(
            String accessToken,
            SendPhoneVerificationNumberRequest request
    ) {
        return given(accessToken)
                .body(request)
                .post("/members/verification/phone/send-number")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 핸드폰_인증번호_확인_요청(
            String accessToken,
            VerifyPhoneRequest request
    ) {
        return given(accessToken)
                .body(request)
                .post("/members/verification/phone/verify")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 회원가입_요청(String accessToken, SignupRequest request) {
        return given(accessToken)
                .body(request)
                .post("/members")
                .then()
                .extract();
    }

    public static MemberResponse 프로필_조회_요청(String accessToken) {
        return given(accessToken)
                .get("/members/my")
                .then()
                .extract()
                .as(MemberResponse.class);
    }

    public static NotificationStatusResponse 푸쉬알림_동의_여부_조회(String accessToken) {
        return given(accessToken)
                .get("/members/notification")
                .then()
                .extract()
                .as(NotificationStatusResponse.class);
    }

    public static ExtractableResponse<Response> 푸쉬알림_동의(String accessToken, NotificationPermitRequest request) {
        return given(accessToken)
                .body(request)
                .post("/members/notification")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 푸쉬알림_거절(String accessToken) {
        return given(accessToken)
                .delete("/members/notification")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 회원정보_수정(String accessToken, MemberUpdateRequest request) {
        return given(accessToken)
                .body(request)
                .put("/members")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_탈퇴(String accessToken, String oauthAccessToken) {
        return given(accessToken)
                .header("OAuthAccessToken", oauthAccessToken)
                .delete("/members")
                .then()
                .extract();
    }
}

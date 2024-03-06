package moa.acceptance.member;

import static moa.acceptance.member.MemberAcceptanceSteps.이메일_중복_체크;
import static moa.acceptance.member.MemberAcceptanceSteps.카카오톡_로그인;
import static moa.acceptance.member.MemberAcceptanceSteps.카카오톡_회원_조회_응답;
import static moa.acceptance.member.MemberAcceptanceSteps.푸쉬알림_거절;
import static moa.acceptance.member.MemberAcceptanceSteps.푸쉬알림_동의;
import static moa.acceptance.member.MemberAcceptanceSteps.푸쉬알림_동의_여부_조회;
import static moa.acceptance.member.MemberAcceptanceSteps.프로필_조회_요청;
import static moa.acceptance.member.MemberAcceptanceSteps.핸드폰_인증번호_전송_요청;
import static moa.acceptance.member.MemberAcceptanceSteps.핸드폰_인증번호_확인_요청;
import static moa.acceptance.member.MemberAcceptanceSteps.회원_탈퇴;
import static moa.acceptance.member.MemberAcceptanceSteps.회원가입_요청;
import static moa.acceptance.member.MemberAcceptanceSteps.회원정보_수정;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import moa.acceptance.AcceptanceTest;
import moa.client.oauth.kakao.KakaoApiClient;
import moa.client.oauth.kakao.response.KakaoWithdrawResponse;
import moa.member.domain.Member;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberSender;
import moa.member.domain.phone.VerificationNumberGenerator;
import moa.member.query.response.MemberResponse;
import moa.member.request.MemberUpdateRequest;
import moa.member.request.NotificationPermitRequest;
import moa.member.request.SendPhoneVerificationNumberRequest;
import moa.member.request.SignupRequest;
import moa.member.request.VerifyPhoneRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("회원 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class MemberAcceptanceTest extends AcceptanceTest {

    @MockBean
    private KakaoApiClient kakaoApiClient;

    @MockBean
    private PhoneVerificationNumberSender phoneVerificationNumberSender;

    @MockBean
    private VerificationNumberGenerator verificationNumberGenerator;

    @Nested
    class Oauth_회원가입_혹은_로그인_API {

        @Test
        void 최초_로그인인_경우_회원가입도_진행된다() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));

            // when
            var accessToken = 카카오톡_로그인("accessToken");

            // then
            assertThat(accessToken).isNotNull();
        }
    }

    @Nested
    class 이메일_중복검사_API {

        @Test
        void 이메일로_가입한_회원이_있는지_확인한다() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            var accessToken = 카카오톡_로그인("accessToken");

            // when
            var response1 = 이메일_중복_체크(accessToken, "email");
            var response2 = 이메일_중복_체크(accessToken, "email2");

            // then
            assertThat(response1).isTrue();
            assertThat(response2).isFalse();
        }
    }

    @Nested
    class 핸드폰_인증번호_전송_API {

        @Test
        void 해당_번호를_사용하는_회원이_없으면_랜덤한_인증번호가_발송된다() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            String accessToken = 카카오톡_로그인("accessToken");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));

            // when
            var response = 핸드폰_인증번호_전송_요청(
                    accessToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );

            // then
            assertThat(response.statusCode()).isEqualTo(200);
            then(phoneVerificationNumberSender)
                    .should(times(1))
                    .sendVerificationNumber(any(), any());
        }

        @Test
        void 이미_다른_회원이_해당_번호로_인증한_경우_예외() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken_mallang"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            given(kakaoApiClient.fetchMember("Bearer accessToken_juno"))
                    .willReturn(카카오톡_회원_조회_응답(
                            2L, "주노", "url", "email2",
                            "2000", "1004", "010-4202-1681"
                    ));
            String mallangToken = 카카오톡_로그인("accessToken_mallang");
            String junoToken = 카카오톡_로그인("accessToken_juno");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));
            핸드폰_인증번호_전송_요청(
                    mallangToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );
            핸드폰_인증번호_확인_요청(
                    mallangToken,
                    new VerifyPhoneRequest("123456")
            );

            // when
            var response = 핸드폰_인증번호_전송_요청(
                    junoToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );

            // then
            assertThat(response.statusCode()).isEqualTo(409);
        }
    }

    @Nested
    class 핸드폰_인증번호_확인_API {

        @Test
        void 전송된_인증번호가_같다면_OK() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            String mallangToken = 카카오톡_로그인("accessToken");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));
            핸드폰_인증번호_전송_요청(
                    mallangToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );

            // when
            var response = 핸드폰_인증번호_확인_요청(
                    mallangToken,
                    new VerifyPhoneRequest("123456")
            );

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }

        @Test
        void 전송된_인증번호와_다르다면_예외() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            String mallangToken = 카카오톡_로그인("accessToken");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));
            핸드폰_인증번호_전송_요청(
                    mallangToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );

            // when
            var response = 핸드폰_인증번호_확인_요청(
                    mallangToken,
                    new VerifyPhoneRequest("111111")
            );

            // then
            assertThat(response.statusCode()).isEqualTo(401);
        }
    }

    @Nested
    class 회원가입_API {

        @Test
        void 핸드폰이_인증되지_않았다면_예외() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            String mallangToken = 카카오톡_로그인("accessToken");
            SignupRequest request = new SignupRequest(
                    "email@email.com",
                    "말랑",
                    "2000",
                    "1004",
                    "url"
            );

            // when
            var response = 회원가입_요청(mallangToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(403);
        }

        @Test
        void 중복된_이메일로_가입한_회원이_있는_경우_예외() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            String mallangToken = 카카오톡_로그인("accessToken");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));
            핸드폰_인증번호_전송_요청(
                    mallangToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );
            핸드폰_인증번호_확인_요청(
                    mallangToken,
                    new VerifyPhoneRequest("123456")
            );
            SignupRequest request = new SignupRequest(
                    "email@email.com",
                    "말랑",
                    "2000",
                    "1004",
                    "url"
            );
            회원가입_요청(mallangToken, request);

            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            2L, "주노", "url", "email",
                            "2000", "1004", "010-3333-3333"
                    ));
            String junoToken = 카카오톡_로그인("accessToken");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));
            핸드폰_인증번호_전송_요청(
                    junoToken,
                    new SendPhoneVerificationNumberRequest("010-3333-3333")
            );
            핸드폰_인증번호_확인_요청(
                    junoToken,
                    new VerifyPhoneRequest("123456")
            );

            // when
            회원가입_요청(junoToken, new SignupRequest(
                    "email@email.com",
                    "주노",
                    "2000",
                    "1004",
                    "url"
            ));

            // when
            var response = 회원가입_요청(junoToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(409);
        }

        @Test
        void 회원가입_완료() {
            // given
            given(kakaoApiClient.fetchMember("Bearer accessToken"))
                    .willReturn(카카오톡_회원_조회_응답(
                            1L, "말랑", "url", "email",
                            "2000", "1004", "010-4202-1681"
                    ));
            String mallangToken = 카카오톡_로그인("accessToken");
            given(verificationNumberGenerator.generate())
                    .willReturn(new PhoneVerificationNumber("123456"));
            핸드폰_인증번호_전송_요청(
                    mallangToken,
                    new SendPhoneVerificationNumberRequest("010-4202-1681")
            );
            핸드폰_인증번호_확인_요청(
                    mallangToken,
                    new VerifyPhoneRequest("123456")
            );
            SignupRequest request = new SignupRequest(
                    "email@email.com",
                    "말랑",
                    "2000",
                    "1004",
                    "url"
            );

            // when
            var response = 회원가입_요청(mallangToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @Nested
    class 내_프로필_조회_API {

        @Test
        void 내_프로필을_조회한다() {
            // given
            Member 준호 = signup("준호", "010-2222-2222");
            String token = login(준호);

            // when
            MemberResponse response = 프로필_조회_요청(token);

            // then
            assertThat(response).isNotNull();
        }
    }

    @Nested
    class 푸쉬알림_허용_상태_확인_API {

        @Test
        void 푸쉬알림_동의_상태를_확인한다() {
            // given
            Member 준호 = signup("준호", "010-2222-2222");
            String junoToken = login(준호);
            Member 말랑 = signup("준호", "010-2222-2222");
            String mallangToken = login(말랑);
            푸쉬알림_동의(junoToken, new NotificationPermitRequest("deviceToken"));

            // when
            var response1 = 푸쉬알림_동의_여부_조회(mallangToken);
            var response2 = 푸쉬알림_동의_여부_조회(junoToken);

            // then
            assertThat(response1.isPermit()).isFalse();
            assertThat(response2.isPermit()).isTrue();
        }
    }

    @Nested
    class 푸쉬알림_허용_API {

        @Test
        void 푸쉬알림을_허용한다() {
            // given
            Member 준호 = signup("준호", "010-2222-2222");
            String token = login(준호);

            // when
            var response = 푸쉬알림_동의(token, new NotificationPermitRequest("deviceToken"));

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @Nested
    class 푸쉬알림_거부_API {

        @Test
        void 푸쉬알림을_거부한다() {
            // given
            Member 준호 = signup("준호", "010-2222-2222");
            String token = login(준호);
            푸쉬알림_동의(token, new NotificationPermitRequest("deviceToken"));

            // when
            var response = 푸쉬알림_거절(token);

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @Nested
    class 회원정보_수정_API {

        @Test
        void 회원정보를_수정한다() {
            // given
            Member 준호 = signup("준호", "010-2222-2222");
            String token = login(준호);

            // when
            var response = 회원정보_수정(token, new MemberUpdateRequest(
                    "변경",
                    "2000",
                    "1004",
                    "변경"
            ));

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @Nested
    class 회원_탈퇴_API {

        @Test
        void 탈퇴_시_진행중인_펀딩은_모두_취소되고_친구관계가_모두_제거된다() {
            // given
            Member 준호 = signup("준호", "010-2222-2222");
            String token = login(준호);
            given(kakaoApiClient.withdrawMember("Bearer accessToken"))
                    .willReturn(new KakaoWithdrawResponse(null));

            // when
            var response = 회원_탈퇴(token, "accessToken");

            // then
            // TODO 펀딩이랑 친구 체크
            assertThat(response.statusCode()).isEqualTo(204);
        }
    }
}

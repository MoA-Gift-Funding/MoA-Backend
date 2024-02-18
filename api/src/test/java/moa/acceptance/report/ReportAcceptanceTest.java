package moa.acceptance.report;

import static moa.acceptance.AcceptanceSupport.assertStatus;
import static moa.acceptance.AcceptanceSupport.given;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import moa.acceptance.AcceptanceTest;
import moa.report.domain.Report.DomainType;
import moa.report.request.ReportWriteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("신고 인수테스트")
public class ReportAcceptanceTest extends AcceptanceTest {

    private String 말랑_token;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_token = login(signup("말랑", "010-2222-2222"));
    }

    @Nested
    class 신고_작성_API {

        @Test
        void 신고를_작성한다() {
            // when
            ExtractableResponse<Response> response = given(말랑_token)
                    .body(new ReportWriteRequest(
                            DomainType.FUNDING,
                            1L,
                            "이사람 이상한 펀딩 만들었어요"
                    ))
                    .post("/reports")
                    .then()
                    .extract();

            // then
            assertStatus(response, CREATED);
        }

        @Test
        void 로그인하지_않으면_신고할_수_없다() {
            // when
            ExtractableResponse<Response> response = given()
                    .body(new ReportWriteRequest(
                            DomainType.FUNDING,
                            1L,
                            "이사람 이상한 펀딩 만들었어요"
                    ))
                    .post("/reports")
                    .then()
                    .extract();

            // then
            assertStatus(response, UNAUTHORIZED);
        }
    }
}

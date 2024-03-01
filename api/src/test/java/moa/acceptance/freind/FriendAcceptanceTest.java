package moa.acceptance.freind;

import static moa.acceptance.AcceptanceSupport.assertStatus;
import static moa.acceptance.AcceptanceSupport.given;
import static moa.acceptance.freind.FriendAcceptanceSteps.내_친구_목록_조회_요청;
import static moa.acceptance.freind.FriendAcceptanceSteps.연락처_동기화;
import static moa.acceptance.freind.FriendAcceptanceSteps.친구_차단_요청;
import static moa.acceptance.freind.FriendAcceptanceSteps.친구_차단_해제_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.common.mapper.TypeRef;
import java.util.List;
import moa.acceptance.AcceptanceTest;
import moa.friend.query.response.FriendResponse;
import moa.friend.request.SyncContactRequest;
import moa.friend.request.SyncContactRequest.ContactRequest;
import moa.friend.request.UpdateFriendRequest;
import moa.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("친구 인수테스트")
public class FriendAcceptanceTest extends AcceptanceTest {

    private Member 말랑;
    private String 말랑_token;
    private Member 준호;
    private String 준호_token;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑 = signup("말랑", "010-1111-1111");
        말랑_token = login(말랑);
        준호 = signup("준호", "010-2222-2222");
        준호_token = login(준호);
    }

    @Nested
    class 연락처_동기화_API {

        @Test
        void 연락처_정보를_바탕으로_친구를_추가한다() {
            // given
            var request = new SyncContactRequest(new ContactRequest("주노", "010-2222-2222"));

            // when
            var response = 연락처_동기화(말랑_token, request);

            // then
            assertStatus(response, OK);
        }

        @Test
        void 연락처에_동일한_번호가_존재하면_둘_중_아무거나_하나의_이름으로_추가된다() {
            // given
            말랑_token = login(말랑);
            var request = new SyncContactRequest(
                    new ContactRequest("주예스", "010-2222-2222"),
                    new ContactRequest("주노", "010-2222-2222"),
                    new ContactRequest("주예스", "010-2222-2222")
            );

            // when
            연락처_동기화(말랑_token, request);

            // then
            var response = 내_친구_목록_조회_요청(말랑_token);
            List<FriendResponse> result = response.as(new TypeRef<>() {
            });
            assertThat(result)
                    .hasSize(1)
                    .element(0)
                    .extracting(FriendResponse::customNickname)
                    .isIn("주노", "주예스");
        }
    }

    @Nested
    class 친구_정보_수정_API {

        @Test
        void 친구의_정보를_수정한다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(new ContactRequest("주노", "010-2222-2222")));
            var 주노_친구_ID = getFriendId(말랑, 준호);
            var request = new UpdateFriendRequest("백엔드 천재 주노");

            // when
            var response = given(말랑_token)
                    .body(request)
                    .put("/friends/{id}", 주노_친구_ID)
                    .then()
                    .extract();

            // then
            assertStatus(response, OK);
        }
    }

    @Nested
    class 친구_차단_API {

        @Test
        void 친구를_차단한다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(new ContactRequest("나쁜놈", "010-2222-2222")));
            var 나쁜놈_친구_ID = getFriendId(말랑, 준호);

            // when
            var response = 친구_차단_요청(말랑_token, 나쁜놈_친구_ID);

            // then
            assertStatus(response, OK);
        }
    }

    @Nested
    class 친구_차단_해제_API {

        @Test
        void 차단한_친구를_차단해제한다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(new ContactRequest("주노", "010-2222-2222")));
            var 주노_친구_ID = getFriendId(말랑, 준호);
            친구_차단_요청(말랑_token, 주노_친구_ID);

            // when
            var response = 친구_차단_해제_요청(말랑_token, 주노_친구_ID);

            // then
            assertStatus(response, OK);
        }
    }

    @Nested
    class 친구_목록_조회_API {

        @Test
        void 내_친구_목록을_차단_여부와_함께_조회한다() {
            // given
            signup("루마", "010-3333-3333");
            연락처_동기화(말랑_token, new SyncContactRequest(
                    new ContactRequest("주노", "010-2222-2222"),
                    new ContactRequest("루마", "010-3333-3333")
            ));
            var 주노_친구_ID = getFriendId(말랑, 준호);
            친구_차단_요청(말랑_token, 주노_친구_ID);

            // when
            var response = 내_친구_목록_조회_요청(말랑_token);

            // then
            List<FriendResponse> result = response.as(new TypeRef<>() {
            });
            assertThat(result)
                    .hasSize(2)
                    .extracting(FriendResponse::customNickname)
                    .containsExactly("주노", "루마");
        }

        @Test
        void 상대방이_나를_차단했더라도_나는_모른다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(
                    new ContactRequest("주노", "010-2222-2222")
            ));
            var 주노_친구_ID = getFriendId(말랑, 준호);
            친구_차단_요청(말랑_token, 주노_친구_ID);

            // when
            var response = 내_친구_목록_조회_요청(준호_token);

            // then
            List<FriendResponse> result = response.as(new TypeRef<>() {
            });
            assertThat(result)
                    .hasSize(1)
                    .extracting(FriendResponse::customNickname)
                    .containsExactly("말랑");
        }
    }
}

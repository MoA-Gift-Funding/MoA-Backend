package moa.acceptance.funding;

import static moa.acceptance.AcceptanceSupport.ID를_추출한다;
import static moa.acceptance.AcceptanceSupport.assertStatus;
import static moa.acceptance.freind.FriendAcceptanceSteps.연락처_동기화;
import static moa.acceptance.freind.FriendAcceptanceSteps.친구_차단_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.나의_펀딩목록_조회_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_메시지_조회_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_목록_조회_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_상세_조회_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_생성_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_참여_요청;
import static moa.fixture.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.common.mapper.TypeRef;
import java.time.LocalDate;
import java.util.List;
import moa.acceptance.AcceptanceTest;
import moa.address.domain.Address;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.friend.request.SyncContactRequest;
import moa.friend.request.SyncContactRequest.ContactRequest;
import moa.funding.domain.MessageVisibility;
import moa.funding.query.response.FundingDetailResponse;
import moa.funding.query.response.FundingResponse;
import moa.funding.request.FundingCreateRequest;
import moa.funding.request.FundingParticipateRequest;
import moa.global.domain.Price;
import moa.global.presentation.PageResponse;
import moa.member.domain.Member;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("펀딩 인수테스트")
public class FundingAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeliveryAddressRepository deliveryRepository;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    private Member 준호;
    private Member 말랑;
    private Product 상품;
    private DeliveryAddress 준호_배송_정보;
    private DeliveryAddress 말랑_배송_정보;
    private String 준호_token;
    private String 말랑_token;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        준호 = signup("준호", "010-2222-2222");
        말랑 = signup("말랑", "010-1234-5678");
        준호_token = login(준호);
        말랑_token = login(말랑);
        상품 = productRepository.save(product("에어팟 맥스", Price.from(700000L)));
        준호_배송_정보 = deliveryRepository.save(new DeliveryAddress(
                준호,
                "준호집",
                "최준호",
                "010-1111-1111",
                new Address(
                        "11111",
                        "도로명",
                        "지번",
                        "상세"
                ),
                true
        ));
        말랑_배송_정보 = deliveryRepository.save(new DeliveryAddress(
                말랑,
                "말랑집",
                "말랑",
                "010-2222-1111",
                new Address(
                        "11112",
                        "도로명",
                        "지번",
                        "상세"
                ),
                true
        ));
    }

    @Nested
    class 펀딩_생성_API {

        @Test
        void 새로운_펀딩을_생성한다() {
            // given
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());

            // when
            var response = 펀딩_생성_요청(준호_token, request);

            // then
            assertStatus(response, CREATED);
        }

        @Test
        void 최대_금액이_펀딩_금액보다_낮으면_400() {
            // given
            var request = new FundingCreateRequest(
                    null,
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "4000",
                    상품.getId(),
                    준호_배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            );

            // when
            var response = 펀딩_생성_요청(준호_token, request);

            // then
            assertStatus(response, BAD_REQUEST);
        }

        @Test
        void 최대_금액이_상품_가격보다_높으면_400() {
            // given
            var request = new FundingCreateRequest(
                    null,
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "800000",
                    상품.getId(),
                    준호_배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            );

            // when
            var response = 펀딩_생성_요청(준호_token, request);

            // then
            assertStatus(response, BAD_REQUEST);
        }
    }

    @Nested
    class 펀딩_조회_API {

        @Test
        void 사용자의_펀딩_목록을_조회한다() {
            // given
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            펀딩_생성_요청(준호_token, request);
            펀딩_생성_요청(준호_token, request);

            // when
            var response = 나의_펀딩목록_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).hasSize(2);
        }

        @Test
        void 펀딩_상세_정보를_조회한다() {
            // given
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));

            // when
            var response = 펀딩_상세_조회_요청(준호_token, fundingId);

            // then
            assertStatus(response, OK);
        }

        @Test
        void 펀딩_상세_정보를_조회한다_참여한_인원의_메시지를_포함한다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));
            var participateRequest = new FundingParticipateRequest(
                    "orderId",
                    "잘~ 먹고갑니다!",
                    MessageVisibility.PUBLIC
            );
            tossPaymentRepository.save(
                    new TossPayment(
                            "temp",
                            participateRequest.paymentOrderId(),
                            "에어팟",
                            "10000",
                            말랑.getId()
                    )
            );
            펀딩_참여_요청(말랑_token, fundingId, participateRequest);

            // when
            var response = 펀딩_상세_조회_요청(준호_token, fundingId);

            // then
            assertStatus(response, OK);
            var detailResponse = response.as(FundingDetailResponse.class);
            assertThat(detailResponse.participants().get(0).message())
                    .isEqualTo(participateRequest.message());
        }

        @Test
        void 펀딩_상세_정보를_조회한다_참여한_인원의_메시지를_포함한다_비공개처리_시_null을_반환한다() {
            // given
            Member 루마 = signup("루마", "010-3333-3333");
            String 루마_token = login(루마);
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678"),
                    new ContactRequest("루마", "010-3333-3333")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));
            var requestWithInvisibleMessage = new FundingParticipateRequest(
                    "orderId",
                    "잘~ 먹고갑니다!",
                    MessageVisibility.PRIVATE
            );
            tossPaymentRepository.save(
                    new TossPayment(
                            "temp",
                            requestWithInvisibleMessage.paymentOrderId(),
                            "에어팟",
                            "10000",
                            말랑.getId()
                    )
            );
            펀딩_참여_요청(말랑_token, fundingId, requestWithInvisibleMessage);

            // when
            var response = 펀딩_상세_조회_요청(루마_token, fundingId);

            // then
            assertStatus(response, OK);
            var detailResponse = response.as(FundingDetailResponse.class);
            assertSoftly(
                    softly -> {
                        softly.assertThat(detailResponse.participants().get(0).memberId()).isNull();
                        softly.assertThat(detailResponse.participants().get(0).message()).isNull();
                        softly.assertThat(detailResponse.participants().get(0).nickName()).isNull();
                        softly.assertThat(detailResponse.participants().get(0).profileImageUrl()).isNull();
                        softly.assertThat(detailResponse.participants().get(0).createAt()).isNotNull();
                    }
            );
        }

        @Test
        void 펀딩_상세_정보를_조회한다_참여한_인원의_메시지를_포함한다_비공개처리여도_개설자가_조회하면_보인다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));
            var requestWithInvisibleMessage = new FundingParticipateRequest(
                    "orderId",
                    "잘~ 먹고갑니다!",
                    MessageVisibility.PRIVATE
            );
            tossPaymentRepository.save(
                    new TossPayment(
                            "temp",
                            requestWithInvisibleMessage.paymentOrderId(),
                            "에어팟",
                            "10000",
                            말랑.getId()
                    )
            );
            펀딩_참여_요청(말랑_token, fundingId, requestWithInvisibleMessage);

            // when
            var response = 펀딩_상세_조회_요청(준호_token, fundingId);

            // then
            assertStatus(response, OK);
            var detailResponse = response.as(FundingDetailResponse.class);
            assertThat(detailResponse.participants().get(0).message())
                    .isEqualTo(requestWithInvisibleMessage.message());
        }

        @Test
        void 펀딩_상세_정보를_조회한다_참여한_인원의_메시지를_포함한다_비공개처리여도_작성자가_조회하면_보인다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));
            var requestWithInvisibleMessage = new FundingParticipateRequest(
                    "orderId",
                    "잘~ 먹고갑니다!",
                    MessageVisibility.PRIVATE
            );
            tossPaymentRepository.save(
                    new TossPayment(
                            "temp",
                            requestWithInvisibleMessage.paymentOrderId(),
                            "에어팟",
                            "10000",
                            말랑.getId()
                    )
            );
            펀딩_참여_요청(말랑_token, fundingId, requestWithInvisibleMessage);

            // when
            var response = 펀딩_상세_조회_요청(말랑_token, fundingId);

            // then
            assertStatus(response, OK);
            var detailResponse = response.as(FundingDetailResponse.class);
            assertThat(detailResponse.participants().get(0).message())
                    .isEqualTo(requestWithInvisibleMessage.message());
        }

        @Test
        void 펀딩_상세_정보를_조회한다_차단되었을_시_조회할_수_없다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(
                    new ContactRequest("최준호 (모아)", "010-2222-2222")
            ));
            Long 준호의_말랑_친구_ID = getFriendId(준호, 말랑);
            친구_차단_요청(준호_token, 준호의_말랑_친구_ID);
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));

            // when
            var response = 펀딩_상세_조회_요청(말랑_token, fundingId);

            // then
            assertStatus(response, FORBIDDEN);
        }

        @Test
        void 펀딩_목록을_조회한다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(
                    new ContactRequest("최준호 (모아)", "010-2222-2222")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            펀딩_생성_요청(준호_token, request);

            // when
            var response = 펀딩_목록_조회_요청(말랑_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).hasSize(1);
        }

        @Test
        void 펀딩_목록을_조회한다_남은_기간이_짧은_순서로_보여진다() {
            // given
            연락처_동기화(말랑_token, new SyncContactRequest(
                    new ContactRequest("최준호 (모아)", "010-2222-2222")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            펀딩_생성_요청(준호_token, new FundingCreateRequest(
                    "",
                    "15일 남은 펀딩",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(15L).toString(),
                    "10000",
                    상품.getId(),
                    준호_배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            ));
            펀딩_생성_요청(준호_token, new FundingCreateRequest(
                    "",
                    "10일 남은 펀딩",
                    "저에게 맥북을 선물할 기회!!",
                    LocalDate.now().plusDays(10L).toString(),
                    "10000",
                    상품.getId(),
                    준호_배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            ));
            펀딩_생성_요청(준호_token, new FundingCreateRequest(
                    "",
                    "20일 남은 펀딩",
                    "저에게 맥북을 선물할 기회!!",
                    LocalDate.now().plusDays(20L).toString(),
                    "10000",
                    상품.getId(),
                    준호_배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            ));

            // when
            var response = 펀딩_목록_조회_요청(말랑_token);

            // then
            assertStatus(response, OK);
            PageResponse<FundingResponse> pageResponse = response.as(new TypeRef<>() {
            });
            assertThat(pageResponse.content()).hasSize(3);
            List<FundingResponse> content = pageResponse.content();
            assertThat(content.get(0).title()).isEqualTo("10일 남은 펀딩");
            assertThat(content.get(2).title()).isEqualTo("20일 남은 펀딩");
        }

        @Test
        void 펀딩_목록을_조회한다_내_펀딩은_보이지_않는다() {
            // given
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            펀딩_생성_요청(준호_token, request);

            // when
            var response = 펀딩_목록_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).isEmpty();
        }

        @Test
        void 펀딩_목록을_조회한다_친구가_아닌사람의_펀딩은_조회되지_않는다() {
            // given
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 말랑_배송_정보.getId());
            펀딩_생성_요청(말랑_token, request);

            // when
            var response = 펀딩_목록_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).isEmpty();
        }

        @Test
        void 펀딩_목록을_조회한다_상대방이_나를_차단한_경우_보이지_않는다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            Long 말랑의_준호_친구_ID = getFriendId(말랑, 준호);
            친구_차단_요청(말랑_token, 말랑의_준호_친구_ID);
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 말랑_배송_정보.getId());
            펀딩_생성_요청(말랑_token, request);

            // when
            var response = 펀딩_목록_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).isEmpty();
        }

        @Test
        void 펀딩_목록을_조회한다_내가_차단한_경우_보이지_않는다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            Long 준호의_말랑_친구_ID = getFriendId(준호, 말랑);
            친구_차단_요청(준호_token, 준호의_말랑_친구_ID);
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 말랑_배송_정보.getId());
            펀딩_생성_요청(말랑_token, request);

            // when
            var response = 펀딩_목록_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).isEmpty();
        }

        @Test
        void 펀딩_목록을_조회한다_내가_펀딩을_올린_친구를_차단했거나_차단당했지_않았다면_친구의_다른_사람과의_차단_여부와_관계없이_조회된다() {
            // given
            Member 루마 = signup("루마", "010-3333-3333");
            String 루마_token = login(루마);
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678"),
                    new ContactRequest("루마", "010-3333-3333")
            ));
            Long 루마의_주노_친구 = getFriendId(루마, 준호);
            친구_차단_요청(루마_token, 루마의_주노_친구);
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            펀딩_생성_요청(준호_token, request);

            // when
            var response = 펀딩_목록_조회_요청(말랑_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).hasSize(1);
        }
    }

    @Nested
    class 펀딩_메시지_조회_API {

        @Test
        void 펀딩_메시지_목록을_조회한다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));
            var participateRequest = new FundingParticipateRequest(
                    "orderId",
                    "잘~ 먹고갑니다!",
                    MessageVisibility.PUBLIC
            );
            tossPaymentRepository.save(
                    new TossPayment(
                            "temp",
                            participateRequest.paymentOrderId(),
                            "에어팟",
                            "10000",
                            말랑.getId()
                    )
            );
            펀딩_참여_요청(말랑_token, fundingId, participateRequest);

            // when
            var response = 펀딩_메시지_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).hasSize(1);
        }

        @Test
        void 펀딩_메시지_목록을_조회한다_비공개처리여도_개설자가_조회하면_보인다() {
            // given
            연락처_동기화(준호_token, new SyncContactRequest(
                    new ContactRequest("신동훈 (모아)", "010-1234-5678")
            ));
            var request = 펀딩_생성_요청_데이터(N일_뒤(5), 준호_배송_정보.getId());
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));
            var requestWithInvisibleMessage = new FundingParticipateRequest(
                    "orderId",
                    "잘~ 먹고갑니다!",
                    MessageVisibility.PRIVATE
            );
            tossPaymentRepository.save(
                    new TossPayment(
                            "temp",
                            requestWithInvisibleMessage.paymentOrderId(),
                            "에어팟",
                            "10000",
                            말랑.getId()
                    )
            );
            펀딩_참여_요청(말랑_token, fundingId, requestWithInvisibleMessage);

            // when
            var response = 펀딩_메시지_조회_요청(준호_token);

            // then
            assertStatus(response, OK);
            var result = response.as(PageResponse.class);
            assertThat(result.content()).hasSize(1);
        }
    }

    private FundingCreateRequest 펀딩_생성_요청_데이터(LocalDate endDate, Long deliveryId) {
        return new FundingCreateRequest(
                "",
                "주노의 에어팟 장만",
                "저에게 에어팟 맥스를 선물할 기회!!",
                endDate.toString(),
                "10000",
                상품.getId(),
                deliveryId,
                "택배함 옆에 놔주세요"
        );
    }

    private LocalDate N일_뒤(int days) {
        return LocalDate.now().plusDays(days);
    }
}

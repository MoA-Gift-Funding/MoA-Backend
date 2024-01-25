package moa.acceptance.funding;

import static moa.acceptance.AcceptanceSupport.ID를_추출한다;
import static moa.acceptance.AcceptanceSupport.assertStatus;
import static moa.acceptance.funding.FundingAcceptanceSteps.나의_펀딩목록_조회_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_상세_조회_요청;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import moa.acceptance.AcceptanceTest;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.funding.domain.Price;
import moa.funding.presentation.request.FundingCreateRequest;
import moa.global.presentation.PageResponse;
import moa.member.domain.Member;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("펀딩 인수테스트 (FundingAcceptance) 은(는)")
public class FundingAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeliveryAddressRepository deliveryRepository;

    private Member 준호;
    private Product 상품;
    private DeliveryAddress 배송_정보;
    private String 준호_token;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        준호 = signup("준호", "010-2222-2222");
        준호_token = login(준호);
        상품 = productRepository.save(new Product("에어팟 맥스", Price.from(700000L)));
        배송_정보 = deliveryRepository.save(new DeliveryAddress(
                준호,
                "준호집",
                "최준호",
                "010-1111-1111",
                "11111",
                "도로명",
                "지번",
                "상세",
                true
        ));
    }

    @Nested
    class 펀딩_생성_API {

        @Test
        void 새로운_펀딩을_생성한다() {
            // given
            var request = new FundingCreateRequest(
                    상품.getId(),
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "10000",
                    배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            );

            // when
            var response = 펀딩_생성_요청(준호_token, request);

            // then
            assertStatus(response, CREATED);
        }

        @Test
        void 최대_금액이_펀딩_금액보다_낮으면_400() {
            // given
            var request = new FundingCreateRequest(
                    상품.getId(),
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "4000",
                    배송_정보.getId(),
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
                    상품.getId(),
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "800000",
                    배송_정보.getId(),
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
            var request = new FundingCreateRequest(
                    상품.getId(),
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "10000",
                    배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            );
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
            var request = new FundingCreateRequest(
                    상품.getId(),
                    "주노의 에어팟 장만",
                    "저에게 에어팟 맥스를 선물할 기회!!",
                    LocalDate.now().plusDays(5L).toString(),
                    "10000",
                    배송_정보.getId(),
                    "택배함 옆에 놔주세요"
            );
            Long fundingId = ID를_추출한다(펀딩_생성_요청(준호_token, request));

            // when
            var response = 펀딩_상세_조회_요청(준호_token, fundingId);

            // then
            assertStatus(response, OK);
        }
    }
}

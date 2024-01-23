package moa.acceptance.funding;

import static moa.acceptance.AcceptanceSupport.assertStatus;
import static moa.acceptance.funding.FundingAcceptanceSteps.펀딩_생성_요청;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.time.LocalDate;
import moa.acceptance.AcceptanceTest;
import moa.funding.domain.Price;
import moa.funding.presentation.request.FundingCreateRequest;
import moa.member.domain.Member;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("펀딩 인수테스트 (FundingAcceptance) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
public class FundingAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    private Member 준호;
    private Product 상품;
    private String 준호_token;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        준호 = signup("준호", "010-2222-2222");
        준호_token = login(준호);
        상품 = productRepository.save(new Product("에어팟 맥스", Price.from(700000L)));
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
                    "주노", "010-1234-5678",
                    "13529", "경기 성남시 분당구 판교역로 166 (카카오 판교 아지트)",
                    "경기 성남시 분당구 백현동 532", "판교 아지트 3층 택배함",
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
                    "주노", "010-1234-5678",
                    "13529", "경기 성남시 분당구 판교역로 166 (카카오 판교 아지트)",
                    "경기 성남시 분당구 백현동 532", "판교 아지트 3층 택배함",
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
                    "주노", "010-1234-5678",
                    "13529", "경기 성남시 분당구 판교역로 166 (카카오 판교 아지트)",
                    "경기 성남시 분당구 백현동 532", "판교 아지트 3층 택배함",
                    "택배함 옆에 놔주세요"
            );

            // when
            var response = 펀딩_생성_요청(준호_token, request);

            // then
            assertStatus(response, BAD_REQUEST);
        }
    }
}

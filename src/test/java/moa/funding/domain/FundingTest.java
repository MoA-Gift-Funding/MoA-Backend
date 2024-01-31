package moa.funding.domain;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.funding.exception.FundingExceptionType.EXCEEDED_POSSIBLE_AMOUNT;
import static moa.funding.exception.FundingExceptionType.INVALID_END_DATE;
import static moa.funding.exception.FundingExceptionType.MAXIMUM_AMOUNT_LESS_THAN_MINIMUM;
import static moa.funding.exception.FundingExceptionType.OWNER_CANNOT_PARTICIPATE;
import static moa.funding.exception.FundingExceptionType.PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT;
import static moa.funding.exception.FundingExceptionType.PRODUCT_PRICE_UNDER_MINIMUM_PRICE;
import static moa.funding.exception.FundingExceptionType.UNDER_MINIMUM_AMOUNT;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import moa.funding.exception.FundingException;
import moa.global.exception.MoaExceptionType;
import moa.member.domain.Member;
import moa.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("펀딩 (Funding) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingTest {

    @Nested
    class 펀딩_생성_시 {

        @Test
        void 펀딩_상품의_가격이_펀딩최소금액보다_낮으면_예외() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("4999")),
                    "5000"
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.create();
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(PRODUCT_PRICE_UNDER_MINIMUM_PRICE);
        }

        @Test
        void 펀딩가능_최대금액이_최소금액보다_낮으면_예외() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("15000")),
                    "4999"
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.create();
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(MAXIMUM_AMOUNT_LESS_THAN_MINIMUM);
        }

        @Test
        void 상품가격이_펀딩가능_최대금액보다_낮으면_예외() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("15000")),
                    "15001"
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.create();
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT);
        }

        @Test
        void 펀딩종료일이_과거인_경우_예외() {
            // given
            Funding funding = new Funding(
                    "",
                    "",
                    LocalDate.now().minusDays(1),
                    Visibility.PUBLIC,
                    Price.from("15000"),
                    mock(Member.class),
                    new Product("", Price.from("15000")),
                    null,
                    ""
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.create();
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(INVALID_END_DATE);
        }

        @Test
        void 성공() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("15000")),
                    "15000"
            );

            // when & then
            assertDoesNotThrow(() -> {
                funding.create();
            });
        }
    }

    @Nested
    class 펀딩_진행률_계산_시 {

        @ParameterizedTest(name = "펀딩 상품이 {0}원일 때, 지금까지 모인 금액이 {1} 원이면, 진행률은 {2}% 다.")
        @CsvSource(value = {
                "300000, 10000, 3",
                "100000, 30000, 30"
        }, delimiterString = ", ")
        void 계산한다(String productPrice, String fundedAmount, String rate) {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from(productPrice)),
                    fundedAmount
            );
            funding.participate(null, Price.from(fundedAmount), "");

            // when
            long fundingRate = funding.getFundingRate();

            // then
            assertThat(fundingRate).isEqualTo(Long.valueOf(rate));
        }
    }

    @Nested
    class 펀딩_참여_시 {

        @Test
        void 펀딩_금액이_펀딩최소금액보다_낮으면_참여할_수_없다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("10000")),
                    "10000"
            );

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(
                        mock(Member.class),
                        Price.from("1000"),
                        "hi"
                );
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(UNDER_MINIMUM_AMOUNT);
        }

        @Test
        void 펀딩_금액이_펀딩최대금액보다_높으면_참여할_수_없다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("100000")),
                    "10000"
            );

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(
                        mock(Member.class),
                        Price.from("10001"),
                        "hi"
                );
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(EXCEEDED_POSSIBLE_AMOUNT);
        }

        @Test
        void 펀딩_금액이_남은_펀딩금액보다_높으면_참여할_수_없다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("16000")),
                    "10000"
            );
            funding.participate(
                    mock(Member.class),
                    Price.from("10000"),
                    "hi"
            );

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(
                        mock(Member.class),
                        Price.from("10000"),
                        "hi"
                );
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(EXCEEDED_POSSIBLE_AMOUNT);
        }

        @Test
        void 펀딩의_남은_금액이_펀딩최소가능금액보다_적을_때_펀딩의_남은_금액과_동일하지_않으면_예외() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("14000")),
                    "10000"
            );
            funding.participate(
                    mock(Member.class),
                    Price.from("10000"),
                    "hi"
            );

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(
                        mock(Member.class),
                        Price.from("3999"),
                        "hi"
                );
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(UNDER_MINIMUM_AMOUNT);
        }

        @Test
        void 펀딩의_남은_금액이_펀딩최소가능금액보다_적을_때_펀딩의_남은_금액과_동일해야_펀딩할_수_있다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("14000")),
                    "10000"
            );
            funding.participate(
                    mock(Member.class),
                    Price.from("10000"),
                    "hi"
            );

            // when
            assertDoesNotThrow(() -> {
                funding.participate(
                        mock(Member.class),
                        Price.from("4000"),
                        "hi"
                );
            });
        }

        @Test
        void 펀딩_개설자는_참여할_수_없다() {
            // given
            Member member = member(1L, "", "", SIGNED_UP);
            Funding funding = funding(
                    member,
                    new Product("", Price.from("14000")),
                    "10000"
            );

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(
                        member,
                        Price.from("3999"),
                        "hi"
                );
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(OWNER_CANNOT_PARTICIPATE);
        }
    }
}

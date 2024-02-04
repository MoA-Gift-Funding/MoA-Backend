package moa.funding.domain;

import static moa.fixture.FundingFixture.funding;
import static moa.fixture.MemberFixture.member;
import static moa.fixture.TossPaymentFixture.tossPayment;
import static moa.funding.domain.FundingStatus.DELIVERY_WAITING;
import static moa.funding.exception.FundingExceptionType.DIFFERENT_FROM_FUNDING_REMAIN_AMOUNT;
import static moa.funding.exception.FundingExceptionType.EXCEEDED_POSSIBLE_FUNDING_AMOUNT;
import static moa.funding.exception.FundingExceptionType.FUNDING_MAXIMUM_AMOUNT_LESS_THAN_MINIMUM;
import static moa.funding.exception.FundingExceptionType.FUNDING_PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT;
import static moa.funding.exception.FundingExceptionType.FUNDING_PRODUCT_PRICE_UNDER_MINIMUM_PRICE;
import static moa.funding.exception.FundingExceptionType.INVALID_FUNDING_END_DATE;
import static moa.funding.exception.FundingExceptionType.MUST_FUNDING_MORE_THNA_MINIMUM_AMOUNT;
import static moa.funding.exception.FundingExceptionType.NO_AUTHORITY_FOR_FINISH_FUNDING;
import static moa.funding.exception.FundingExceptionType.OWNER_CANNOT_PARTICIPATE_FUNDING;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import moa.funding.exception.FundingException;
import moa.global.domain.Price;
import moa.global.exception.MoaExceptionType;
import moa.member.domain.Member;
import moa.pay.domain.TossPayment;
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
            assertThat(exceptionType).isEqualTo(FUNDING_PRODUCT_PRICE_UNDER_MINIMUM_PRICE);
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
            assertThat(exceptionType).isEqualTo(FUNDING_MAXIMUM_AMOUNT_LESS_THAN_MINIMUM);
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
            assertThat(exceptionType).isEqualTo(FUNDING_PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT);
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
            assertThat(exceptionType).isEqualTo(INVALID_FUNDING_END_DATE);
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
            TossPayment payment = tossPayment(fundedAmount, 1L);
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from(productPrice)),
                    fundedAmount
            );
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");
            funding.participate(participant);

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
            TossPayment payment = tossPayment("1000", 1L);
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(participant);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(MUST_FUNDING_MORE_THNA_MINIMUM_AMOUNT);
        }

        @Test
        void 펀딩_금액이_펀딩최대금액보다_높으면_참여할_수_없다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("100000")),
                    "10000"
            );
            TossPayment payment = tossPayment("10001", 1L);
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(participant);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(EXCEEDED_POSSIBLE_FUNDING_AMOUNT);
        }

        @Test
        void 펀딩_금액이_남은_펀딩금액보다_높으면_참여할_수_없다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("16000")),
                    "10000"
            );
            TossPayment payment = tossPayment("10000", 1L);
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");
            funding.participate(participant);
            TossPayment payment2 = tossPayment("10000", 1L);
            var participant2 = new FundingParticipant(mock(Member.class), funding, payment2, "");

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(participant2);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(EXCEEDED_POSSIBLE_FUNDING_AMOUNT);
        }

        @Test
        void 펀딩의_남은_금액이_펀딩최소가능금액보다_적을_때_펀딩의_남은_금액과_동일하지_않으면_예외() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("14000")),
                    "10000"
            );
            TossPayment payment = tossPayment("10000", 1L);
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");
            funding.participate(participant);
            TossPayment payment2 = tossPayment("3999", 1L);
            var participant2 = new FundingParticipant(mock(Member.class), funding, payment2, "");

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(participant2);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(MUST_FUNDING_MORE_THNA_MINIMUM_AMOUNT);
        }

        @Test
        void 펀딩의_남은_금액이_펀딩최소가능금액보다_적을_때_펀딩의_남은_금액과_동일해야_펀딩할_수_있다() {
            // given
            Funding funding = funding(
                    mock(Member.class),
                    new Product("", Price.from("14000")),
                    "10000"
            );
            TossPayment payment = tossPayment("10000", 1L);
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");
            funding.participate(participant);
            TossPayment payment2 = tossPayment("4000", 1L);
            var participant2 = new FundingParticipant(mock(Member.class), funding, payment2, "");

            // when
            assertDoesNotThrow(() -> {
                funding.participate(participant2);
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
            TossPayment payment = tossPayment("10000", 1L);
            var participant = new FundingParticipant(member, funding, payment, "");

            // when
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.participate(participant);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(OWNER_CANNOT_PARTICIPATE_FUNDING);
        }

        @Test
        void 펀딩_상품_가격을_모두_채우면_펀딩이_완료된다() {
            Member member = member(1L, "", "", SIGNED_UP);
            Funding funding = funding(
                    member,
                    new Product("", Price.from("10000")),
                    "10000"
            );
            TossPayment payment = tossPayment("10000", 1L);
            var participant = new FundingParticipant(mock(Member.class), funding, payment, "");

            // when
            funding.participate(participant);

            // then
            assertThat(funding.getStatus()).isEqualTo(DELIVERY_WAITING);
        }
    }

    @Nested
    class 펀딩_끝내기_시 {

        @Test
        void 주인이_아닌_다른_사람은_펀딩을_끝낼_수_없다() {
            // given
            Member member = member(1L, "", "", SIGNED_UP);
            Funding funding = funding(
                    member,
                    new Product("", Price.from("10000")),
                    "10000"
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.finish(mock(Member.class), Price.from("10000"));
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(NO_AUTHORITY_FOR_FINISH_FUNDING);
        }

        @Test
        void 추가할_금액이_남은_금액과_일치하지_않으면_예외() {
            // given
            Member member = member(1L, "", "", SIGNED_UP);
            Funding funding = funding(
                    member,
                    new Product("", Price.from("10000")),
                    "10000"
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(FundingException.class, () -> {
                funding.finish(member, Price.from("10001"));
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(DIFFERENT_FROM_FUNDING_REMAIN_AMOUNT);
        }

        @Test
        void 주인이_펀딩을_끝낸다() {
            // given
            Member member = member(1L, "", "", SIGNED_UP);
            Funding funding = funding(
                    member,
                    new Product("", Price.from("10000")),
                    "10000"
            );

            // when & then
            assertDoesNotThrow(() -> {
                funding.finish(member, Price.from("10000"));
            });
            assertThat(funding.getStatus()).isEqualTo(DELIVERY_WAITING);
        }
    }
}

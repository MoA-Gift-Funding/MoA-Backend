package moa.pay.application;

import static moa.pay.domain.TossPaymentStatus.CANCELED;
import static moa.pay.domain.TossPaymentStatus.PENDING_CANCEL;
import static moa.pay.exception.TossPaymentExceptionType.ALREADY_CANCELED_PAYMENT;
import static moa.pay.exception.TossPaymentExceptionType.TOSS_API_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import moa.ApplicationTest;
import moa.global.exception.MoaExceptionType;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentResponse;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
import moa.pay.exception.TossPaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("토스 결제 서비스 (TossPaymentService) 은(는)")
class TossPaymentServiceTest {

    @Autowired
    private TossPaymentService tossPaymentService;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @MockBean
    private TossClient tossClient;

    @Nested
    class 결제_취소_시 {

        TossPayment payment;

        @BeforeEach
        void setUp() {
            payment = tossPaymentRepository.save(new TossPayment(
                    "1",
                    "2",
                    "3",
                    "10000",
                    1L
            ));
        }

        @Test
        void 토스_api에서_예외가_발생하면_결제_정보는_결제_대기_상태로_남는다() {
            // given
            willThrow(new TossPaymentException(TOSS_API_ERROR))
                    .given(tossClient)
                    .cancelPayment(any());

            // when
            MoaExceptionType exceptionType = assertThrows(TossPaymentException.class, () -> {
                tossPaymentService.cancelPayment(
                        payment.getOrderId(),
                        "그냥"
                );
            }).getExceptionType();

            // then
            assertThat(exceptionType).isEqualTo(TOSS_API_ERROR);
            TossPayment after = tossPaymentRepository.getByOrderId(payment.getOrderId());
            assertThat(after.getStatus()).isEqualTo(PENDING_CANCEL);
        }

        @Test
        void 결제_성공_시_결제_상태는_취소_상태가_된다() {
            // when
            tossPaymentService.cancelPayment(
                    payment.getOrderId(),
                    "그냥"
            );

            // then
            TossPayment after = tossPaymentRepository.getByOrderId(payment.getOrderId());
            assertThat(after.getStatus()).isEqualTo(CANCELED);
        }

        @Test
        void 결제_취소시_실패한_경우_다시_요청할_수_있다() {
            // given
            willThrow(new TossPaymentException(TOSS_API_ERROR))
                    .given(tossClient)
                    .cancelPayment(any());
            try {
                tossPaymentService.cancelPayment(
                        payment.getOrderId(),
                        "그냥"
                );
            } catch (Exception e) {
            }

            willReturn(mock(TossPaymentResponse.class))
                    .given(tossClient)
                    .cancelPayment(any());

            // when
            tossPaymentService.cancelPayment(
                    payment.getOrderId(),
                    "그냥"
            );

            // then
            TossPayment after = tossPaymentRepository.getByOrderId(payment.getOrderId());
            assertThat(after.getStatus()).isEqualTo(CANCELED);
        }

        @Test
        void 결제_취소는_두번_될_수_없다() {
            // given
            tossPaymentService.cancelPayment(
                    payment.getOrderId(),
                    "그냥"
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(TossPaymentException.class, () -> {
                tossPaymentService.cancelPayment(
                        payment.getOrderId(),
                        "그냥"
                );
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(ALREADY_CANCELED_PAYMENT);
        }
    }
}

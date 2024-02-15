package moa.pay;

import static moa.fixture.TossPaymentFixture.tossPayment;
import static moa.pay.domain.TossPaymentStatus.CANCELED;
import static moa.pay.domain.TossPaymentStatus.PENDING_CANCEL;
import static moa.pay.domain.TossPaymentStatus.UNUSED;
import static moa.pay.domain.TossPaymentStatus.USED;
import static moa.pay.exception.TossPaymentExceptionType.TOSS_API_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDateTime;
import moa.BatchTest;
import moa.pay.client.PaymentProperty;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentCancelRequest;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentCancel;
import moa.pay.domain.TossPaymentRepository;
import moa.pay.domain.TossPaymentStatus;
import moa.pay.exception.TossPaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@BatchTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PaymentCancelJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job paymentCancelJob;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Autowired
    private PaymentProperty paymentProperty;

    @MockBean
    private TossClient tossClient;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(paymentCancelJob);
    }

    @Test
    void 취소_대기중인_결제_중_멱등키_생성_후_10일이_지난_결제의_멱등키를_재발급한뒤_결제를_취소한다() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        TossPayment 결제대기_1_멱등키_생성후_12일_지남 = 결제정보("10000", 1L, PENDING_CANCEL, now.minusDays(12));
        TossPayment 결제대기_2_멱등키_생성후_5일_지남 = 결제정보("10000", 1L, PENDING_CANCEL, now.minusDays(5));
        TossPayment 결제대기_3 = 결제정보("10000", 1L, PENDING_CANCEL, now.minusDays(1));
        TossPayment 안사용 = 결제정보("10000", 1L, UNUSED);
        TossPayment 사용 = 결제정보("10000", 1L, USED);
        TossPayment 취소완료 = 결제정보("10000", 1L, CANCELED);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        // when
        jobLauncherTestUtils.launchJob(jobParameters);

        // then
        멱등키_변경_확인(결제대기_1_멱등키_생성후_12일_지남, now);
        멱등키_변경되지_않음(결제대기_2_멱등키_생성후_5일_지남, now);
        멱등키_변경되지_않음(결제대기_3, now);
        멱등키_변경되지_않음(안사용, now);
        멱등키_변경되지_않음(사용, now);
        멱등키_변경되지_않음(취소완료, now);
        결제_상태_확인(결제대기_1_멱등키_생성후_12일_지남, CANCELED);
        결제_상태_확인(결제대기_2_멱등키_생성후_5일_지남, CANCELED);
        결제_상태_확인(결제대기_3, CANCELED);
        결제_상태_확인(안사용, UNUSED);
        결제_상태_확인(사용, USED);
    }

    @Test
    void 토스_api에서_예외가_발생하면_해당_결제정보를_건너뛰고_계속해서_진행한다() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        TossPayment 결제대기_1 = 결제정보("10000", 1L, PENDING_CANCEL, now.minusDays(1));
        TossPayment 결제대기_2 = 결제정보("10000", 1L, PENDING_CANCEL, now.minusDays(2));

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();
        willThrow(new TossPaymentException(TOSS_API_ERROR))
                .given(tossClient).cancelPayment(
                        결제대기_1.getPaymentKey(),
                        paymentProperty.basicAuth(),
                        결제대기_1.getIdempotencyKeyForCancel(),
                        new TossPaymentCancelRequest(결제대기_1.getCancel().getReason())
                );

        // when
        jobLauncherTestUtils.launchJob(jobParameters);

        // then
        결제_상태_확인(결제대기_1, PENDING_CANCEL);
        결제_상태_확인(결제대기_2, CANCELED);
    }

    private void 결제_상태_확인(TossPayment payment, TossPaymentStatus status) {
        assertThat(tossPaymentRepository.getByOrderId(payment.getOrderId()).getStatus())
                .isEqualTo(status);
    }

    private TossPayment 결제정보(String amount, long memberId, TossPaymentStatus status, LocalDateTime keyUpdatedDate) {
        TossPayment payment = tossPayment(amount, memberId, status);
        setField(payment.getCancel(), "idKeyUpdatedDate", keyUpdatedDate);
        return tossPaymentRepository.save(payment);
    }

    private TossPayment 결제정보(String amount, long memberId, TossPaymentStatus status) {
        return tossPaymentRepository.save(tossPayment(amount, memberId, status));
    }

    private void 멱등키_변경_확인(TossPayment payment, LocalDateTime now) {
        TossPaymentCancel update = tossPaymentRepository.getByOrderId(payment.getOrderId())
                .getCancel();
        assertThat(update.getIdempotencyKey())
                .isNotEqualTo(payment.getCancel().getIdempotencyKey());
        assertThat(update.getIdKeyUpdatedDate())
                .isEqualTo(now);
    }

    private void 멱등키_변경되지_않음(TossPayment payment, LocalDateTime now) {
        TossPaymentCancel update = tossPaymentRepository.getByOrderId(payment.getOrderId()).getCancel();
        if (update == null) {
            return;
        }
        assertThat(update.getIdempotencyKey())
                .isEqualTo(payment.getCancel().getIdempotencyKey());
        assertThat(update.getIdKeyUpdatedDate())
                .isNotEqualTo(now);
    }
}

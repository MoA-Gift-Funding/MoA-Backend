package moa.funding.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.funding.application.command.FundingFinishCommand;
import moa.funding.application.command.FundingParticipateCommand;
import moa.pay.application.TossPaymentService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingFacade {

    private final FundingService fundingService;
    private final TossPaymentService tossPaymentService;

    public void participate(FundingParticipateCommand command) {
        try {
            fundingService.participate(command);
        } catch (Exception e) {
            log.error("펀딩 참여 실패", e);
            tossPaymentService.cancelPayment(
                    command.paymentOrderId(),
                    "펀딩 참여 실패"
            );
        }
    }

    public void finish(FundingFinishCommand command) {
        try {
            fundingService.finish(command);
        } catch (Exception e) {
            log.error("펀딩 끝내기 실패", e);
            tossPaymentService.cancelPayment(
                    command.paymentOrderId(),
                    "펀딩 끝내기 실패"
            );
        }
    }
}

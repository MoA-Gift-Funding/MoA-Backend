package moa.funding.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.member.domain.MemberWithdrawnEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FundingEventHandler {

    private final FundingRepository fundingRepository;

    @EventListener(MemberWithdrawnEvent.class)
    public void cancelFunding(MemberWithdrawnEvent event) {
        List<Funding> fundings = fundingRepository.findAllCancellableByMemberId(event.memberId());
        for (Funding funding : fundings) {
            funding.cancel();
            fundingRepository.save(funding);
        }
    }
}

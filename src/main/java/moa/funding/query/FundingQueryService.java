package moa.funding.query;

import lombok.RequiredArgsConstructor;
import moa.funding.query.response.MyFundingsResponse.MyFundingDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FundingQueryService {

    private final FundingQueryRepository fundingQueryRepository;

    public Page<MyFundingDetail> findMyFundings(Long memberId, Pageable pageable) {
        return fundingQueryRepository.findAllByMemberId(memberId, pageable)
                .map(MyFundingDetail::from);
    }
}

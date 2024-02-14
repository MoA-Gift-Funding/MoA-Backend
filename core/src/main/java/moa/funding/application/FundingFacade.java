package moa.funding.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FundingFacade {

    private final FundingService fundingService;
}

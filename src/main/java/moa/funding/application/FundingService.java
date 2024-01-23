package moa.funding.application;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingValidator;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class FundingService {

    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final FundingValidator fundingValidator;

    public Funding create(FundingCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Funding funding = new Funding(
            command.title(),
            command.description(),
            command.endDate(),
            command.maximumPrice(),
            command.minimumPrice(),
            command.deliveryAddress(),
            command.visible(),
            command.status(),
            member
        );
        fundingValidator.validateFundingPrice(funding);
        return fundingRepository.save(funding);
    }
}

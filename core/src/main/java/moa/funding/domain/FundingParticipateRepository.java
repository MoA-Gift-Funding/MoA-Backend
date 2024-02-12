package moa.funding.domain;

import static moa.funding.exception.FundingExceptionType.NOT_PARTICIPATING_FUNDING;

import java.util.Optional;
import moa.funding.exception.FundingException;
import moa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingParticipateRepository extends JpaRepository<FundingParticipant, Long> {

    default FundingParticipant getByFundingAndMember(Funding funding, Member member) {
        return findByFundingAndMember(funding, member).orElseThrow(() ->
                new FundingException(NOT_PARTICIPATING_FUNDING)
        );
    }

    Optional<FundingParticipant> findByFundingAndMember(Funding funding, Member member);
}

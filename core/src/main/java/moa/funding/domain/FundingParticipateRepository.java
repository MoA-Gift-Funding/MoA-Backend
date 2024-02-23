package moa.funding.domain;

import static moa.funding.exception.FundingExceptionType.NOT_FOUND_PARTICIPANT;

import moa.funding.exception.FundingException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingParticipateRepository extends JpaRepository<FundingParticipant, Long> {

    default FundingParticipant getById(Long id) {
        return findById(id).orElseThrow(() ->
                new FundingException(NOT_FOUND_PARTICIPANT)
        );
    }
}

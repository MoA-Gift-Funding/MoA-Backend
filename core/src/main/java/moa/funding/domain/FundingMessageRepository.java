package moa.funding.domain;

import static moa.funding.exception.FundingExceptionType.NOT_FOUND_MESSAGE;

import moa.funding.exception.FundingException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingMessageRepository extends JpaRepository<FundingMessage, Long> {

    default FundingMessage getById(Long id) {
        return findById(id).orElseThrow(() ->
                new FundingException(NOT_FOUND_MESSAGE)
        );
    }
}

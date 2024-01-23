package moa.funding.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import moa.funding.exception.FundingException;
import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

public interface FundingRepository extends JpaRepository<Funding, Long> {

    default Funding getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }
}

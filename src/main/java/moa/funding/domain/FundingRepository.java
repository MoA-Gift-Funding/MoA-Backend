package moa.funding.domain;

import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

import moa.funding.exception.FundingException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding, Long> {

    default Funding getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }
}

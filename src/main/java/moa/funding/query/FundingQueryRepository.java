package moa.funding.query;

import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

import moa.funding.domain.Funding;
import moa.funding.exception.FundingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingQueryRepository extends JpaRepository<Funding, Long> {

    default Funding getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }

    Page<Funding> findAllByMemberId(Long memberId, Pageable pageable);
}

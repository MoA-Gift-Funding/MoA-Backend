package moa.funding.domain;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

import java.util.List;
import java.util.Optional;
import moa.funding.exception.FundingException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface FundingRepository extends JpaRepository<Funding, Long> {

    default Funding getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }

    default Funding getWithLockById(Long id) {
        return findWithLockById(id)
                .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }

    @Lock(PESSIMISTIC_WRITE)
    Optional<Funding> findWithLockById(Long id);

    @Query("SELECT f FROM Funding f WHERE f.status != 'PROCESSING' AND f.status != 'STOPPED'")
    List<Funding> findAllCancellableByMemberId(Long memberId);
}

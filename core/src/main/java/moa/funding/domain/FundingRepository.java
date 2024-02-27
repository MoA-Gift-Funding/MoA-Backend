package moa.funding.domain;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

import java.util.List;
import java.util.Optional;
import moa.funding.exception.FundingException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface FundingRepository extends JpaRepository<Funding, Long> {

    default Funding getWithParticipantsById(Long id) {
        return findWithParticipantsById(id)
                .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }

    @EntityGraph(attributePaths = {"participants"})
    Optional<Funding> findWithParticipantsById(Long id);

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

    List<Funding> findAllByMemberId(Long memberId);
}

package moa.funding.query;

import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

import java.util.List;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingStatus;
import moa.funding.exception.FundingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FundingQueryRepository extends JpaRepository<Funding, Long> {

    default Funding getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new FundingException(NOT_FOUND_FUNDING));
    }

    Page<Funding> findAllByMemberId(Long memberId, Pageable pageable);

    @Query("""
            SELECT f FROM Funding f
             
            LEFT JOIN Friend friend
                ON f.member.id = friend.target.id
                AND friend.member.id = :memberId
                AND friend.isBlocked = FALSE

            WHERE
            f.status IN (:statuses)
            AND (
                f.member.id = friend.target.id
                AND NOT EXISTS (
                    SELECT 1
                    FROM Friend blockedFriend
                    WHERE f.member.id = blockedFriend.member.id
                        AND blockedFriend.target.id = :memberId
                        AND blockedFriend.isBlocked = TRUE
                    )
                )
            AND f.member.id != :memberId
            """)
    Page<Funding> findByUnblockedFriends(
            @Param("memberId") Long memberId,
            @Param("statuses") List<FundingStatus> statuses,
            Pageable pageable
    );
}

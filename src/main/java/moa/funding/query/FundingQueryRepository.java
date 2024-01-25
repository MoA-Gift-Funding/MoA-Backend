package moa.funding.query;

import static moa.funding.exception.FundingExceptionType.NOT_FOUND_FUNDING;

import moa.funding.domain.Funding;
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
            JOIN Friend friend ON f.member.id = friend.target.id AND friend.member.id = :memberId
            WHERE NOT EXISTS (
                SELECT 1
                FROM Friend blockedFriend
                WHERE (f.member.id = blockedFriend.member.id OR f.member.id = blockedFriend.target.id)
                  AND blockedFriend.isBlocked = TRUE
            )
            """
    )
    Page<Funding> findMembersFriend(@Param(value = "memberId") Long memberId, Pageable pageable);
}

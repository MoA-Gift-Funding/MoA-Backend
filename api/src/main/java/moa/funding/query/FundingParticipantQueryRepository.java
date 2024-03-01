package moa.funding.query;

import moa.funding.domain.FundingParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FundingParticipantQueryRepository extends JpaRepository<FundingParticipant, Long> {

    Page<FundingParticipant> findAllByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT p FROM FundingParticipant p JOIN FETCH p.fundingMessage WHERE p.funding.member.id = :memberId AND p.status = 'PARTICIPATING' ")
    Page<FundingParticipant> findAllByFundingOwnerIdAndStatusIsParticipating(
            @Param("memberId") Long memberId,
            Pageable pageable
    );
}

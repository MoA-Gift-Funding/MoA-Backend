package moa.funding.query;

import moa.funding.domain.FundingParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingParticipantQueryRepository extends JpaRepository<FundingParticipant, Long> {

    Page<FundingParticipant> findAllByMemberId(Long memberId, Pageable pageable);
}

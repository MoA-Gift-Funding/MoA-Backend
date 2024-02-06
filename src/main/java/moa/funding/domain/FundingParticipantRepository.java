package moa.funding.domain;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingParticipantRepository extends JpaRepository<FundingParticipant, Long> {

    @EntityGraph(attributePaths = "tossPayment")
    List<FundingParticipant> findWithPaymentByFundingId(Long fundingId);
}

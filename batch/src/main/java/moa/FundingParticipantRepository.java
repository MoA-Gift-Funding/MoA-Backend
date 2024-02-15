package moa;

import java.util.List;
import moa.funding.domain.FundingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingParticipantRepository extends JpaRepository<FundingParticipant, Long> {

    @Query("""
            select fp
                from FundingParticipant fp
                join fetch fp.funding f
                join fetch fp.member m
                join fetch fp.tossPayment tp
            """)
    List<FundingParticipant> findAll();
}

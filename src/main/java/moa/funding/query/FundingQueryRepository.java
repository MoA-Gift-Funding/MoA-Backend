package moa.funding.query;

import moa.funding.domain.Funding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingQueryRepository extends JpaRepository<Funding, Long> {

    Page<Funding> findAllByMemberId(Long memberId, Pageable pageable);
}

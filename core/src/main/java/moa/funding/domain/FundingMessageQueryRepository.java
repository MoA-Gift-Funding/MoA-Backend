package moa.funding.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingMessageQueryRepository extends JpaRepository<FundingMessage, Long> {

    Page<FundingMessage> findAllByReceiverId(Long memberId, Pageable pageable);
}

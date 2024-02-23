package moa.customerservicecenter.query;

import moa.customerservicecenter.domain.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQQueryRepository extends JpaRepository<FAQ, Long> {
}

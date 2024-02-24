package moa.cs.query;

import moa.cs.domain.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQQueryRepository extends JpaRepository<FAQ, Long> {
}

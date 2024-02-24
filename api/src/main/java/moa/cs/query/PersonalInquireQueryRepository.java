package moa.cs.query;

import java.util.List;
import moa.cs.domain.PersonalInquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalInquireQueryRepository extends JpaRepository<PersonalInquiry, Long> {

    List<PersonalInquiry> findByMemberId(Long memberId);
}

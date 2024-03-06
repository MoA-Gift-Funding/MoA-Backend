package moa.member.query;

import static moa.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import moa.member.domain.Member;
import moa.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQueryRepository extends JpaRepository<Member, Long> {

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    boolean existsByEmail(String email);
}

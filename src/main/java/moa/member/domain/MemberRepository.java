package moa.member.domain;

import static moa.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import java.util.List;
import java.util.Optional;
import moa.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    Optional<Member> findByOauthId(OauthId oauthId);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.phone.phoneNumber = :phoneNumber AND m.phone.verified = TRUE")
    boolean existsByVerifiedPhone(String phoneNumber);

    @Query("SELECT m FROM Member m WHERE m.phone.phoneNumber IN (:phoneNumbers)")
    List<Member> findByPhoneNumberIn(List<String> phoneNumbers);
}

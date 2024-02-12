package moa.friend.domain;

import static moa.friend.exception.FriendExceptionType.NOT_FOUND_FRIEND;

import java.util.List;
import java.util.Optional;
import moa.friend.exception.FriendException;
import moa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    default Friend getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new FriendException(NOT_FOUND_FRIEND));
    }

    List<Friend> findByMemberAndTargetIn(Member member, List<Member> targets);

    Optional<Friend> findByMemberAndTarget(Member member, Member target);
}

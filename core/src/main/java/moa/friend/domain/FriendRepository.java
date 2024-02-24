package moa.friend.domain;

import static moa.friend.exception.FriendExceptionType.NOT_FOUND_FRIEND;

import java.util.List;
import java.util.Optional;
import moa.friend.exception.FriendException;
import moa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    default Friend getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new FriendException(NOT_FOUND_FRIEND));
    }

    List<Friend> findByMemberAndTargetIn(Member member, List<Member> targets);

    default Friend getByMemberAndTarget(Member member, Member target) {
        return findByMemberAndTarget(member, target)
                .orElseThrow(() -> new FriendException(NOT_FOUND_FRIEND));
    }

    Optional<Friend> findByMemberAndTarget(Member member, Member target);

    @Query("SELECT f FROM Friend f JOIN FETCH f.member WHERE f.target = :member")
    List<Friend> findAllByTargetId(@Param("member") Member member);

    @Query("SELECT f FROM Friend f JOIN FETCH f.member WHERE f.member = :member AND f.isBlocked = FALSE")
    List<Friend> findUnblockedByMemberId(@Param("member") Member member);

    @Query("SELECT f FROM Friend f JOIN FETCH f.member WHERE f.target = :member AND f.isBlocked = FALSE")
    List<Friend> findUnblockedByTargetId(@Param("member") Member member);
}

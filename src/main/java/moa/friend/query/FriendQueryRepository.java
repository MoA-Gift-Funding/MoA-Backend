package moa.friend.query;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import moa.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendQueryRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f JOIN FETCH f.member WHERE f.member.id = :memberId AND f.isBlocked = FALSE")
    List<Friend> findUnblockedByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT f FROM Friend f JOIN FETCH f.member WHERE f.member.id = :memberId AND f.isBlocked = TRUE")
    List<Friend> findBlockedByMemberId(@Param("memberId") Long memberId);
}

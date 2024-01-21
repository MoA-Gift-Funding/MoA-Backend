package moa.friend.query;

import java.util.List;
import moa.friend.domain.Friend;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendQueryRepository extends JpaRepository<Friend, Long> {

    @EntityGraph(attributePaths = {"target"})
    List<Friend> findByMemberId(Long memberId);
}

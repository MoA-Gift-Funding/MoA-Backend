package moa.friend.domain;

import java.util.List;
import moa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByMemberAndTargetIn(Member member, List<Member> targets);
}

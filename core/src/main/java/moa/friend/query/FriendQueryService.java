package moa.friend.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.query.response.FriendResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendQueryService {

    private final FriendQueryRepository friendQueryRepository;

    public List<FriendResponse> findFriendsByMemberId(Long memberId) {
        List<Friend> friends = friendQueryRepository.findAllByMemberId(memberId);
        return FriendResponse.from(friends);
    }
}

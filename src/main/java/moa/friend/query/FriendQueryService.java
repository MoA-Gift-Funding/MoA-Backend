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

    public List<FriendResponse> findUnblockedFriendsByMemberId(Long memberId) {
        List<Friend> friends = friendQueryRepository.findUnblockedByMemberId(memberId);
        return FriendResponse.from(friends);
    }

    public List<FriendResponse> findBlockedFriendsByMemberId(Long memberId) {
        List<Friend> friends = friendQueryRepository.findBlockedByMemberId(memberId);
        return FriendResponse.from(friends);
    }
}

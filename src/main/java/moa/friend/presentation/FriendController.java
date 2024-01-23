package moa.friend.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.friend.application.FriendService;
import moa.friend.presentation.request.SyncContactRequest;
import moa.friend.query.FriendQueryService;
import moa.friend.query.response.FriendResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/friends")
@RestController
public class FriendController implements FriendApi {

    private final FriendService friendService;
    private final FriendQueryService friendQueryService;

    @PostMapping("/sync-contact")
    public ResponseEntity<Void> syncContact(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @RequestBody SyncContactRequest request
    ) {
        friendService.makeFromContact(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<FriendResponse>> findMyFriends(
            @Auth(permit = {SIGNED_UP}) Long memberId
    ) {
        List<FriendResponse> result = friendQueryService.findFriendsByMemberId(memberId);
        return ResponseEntity.ok(result);
    }
}

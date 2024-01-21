package moa.friend.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.friend.application.FriendService;
import moa.friend.presentation.request.SyncContactRequest;
import moa.friend.query.FriendQueryService;
import moa.friend.query.response.FriendResponse;
import moa.global.auth.Auth;
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
            @Auth Long memberId,
            @RequestBody SyncContactRequest request
    ) {
        friendService.makeFromContact(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<FriendResponse>> findMyFriends(@Auth Long memberId) {
        List<FriendResponse> result = friendQueryService.findFriendsByMemberId(memberId);
        return ResponseEntity.ok(result);
    }
}

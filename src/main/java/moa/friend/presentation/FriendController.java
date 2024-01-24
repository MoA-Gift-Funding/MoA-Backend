package moa.friend.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.friend.application.FriendService;
import moa.friend.presentation.request.SyncContactRequest;
import moa.friend.presentation.request.UpdateFriendRequest;
import moa.friend.query.FriendQueryService;
import moa.friend.query.response.FriendResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
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

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("id") Long friendId,
            @RequestBody UpdateFriendRequest request
    ) {
        friendService.update(request.toCommand(memberId, friendId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block/{id}")
    public ResponseEntity<Void> block(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("id") Long friendId
    ) {
        friendService.block(memberId, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock/{id}")
    public ResponseEntity<Void> unblock(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("id") Long friendId
    ) {
        friendService.unblock(memberId, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<FriendResponse>> findMyFriends(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @RequestParam(name = "isBlocked") boolean isBlocked
    ) {
        if (isBlocked) {
            return ResponseEntity.ok(friendQueryService.findBlockedFriendsByMemberId(memberId));
        }
        return ResponseEntity.ok(friendQueryService.findUnblockedFriendsByMemberId(memberId));
    }
}

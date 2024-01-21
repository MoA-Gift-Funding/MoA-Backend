package moa.friend.presentation;

import lombok.RequiredArgsConstructor;
import moa.friend.application.FriendService;
import moa.friend.presentation.request.SyncContactRequest;
import moa.global.auth.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/friends")
@RestController
public class FriendController implements FriendApi {

    private final FriendService friendService;

    @PostMapping("/sync-contact")
    public ResponseEntity<Void> syncContact(
            @Auth Long memberId,
            @RequestBody SyncContactRequest request
    ) {
        friendService.makeFromContact(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }
}

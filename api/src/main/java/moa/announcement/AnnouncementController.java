package moa.announcement;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.announcement.query.AnnouncementQueryService;
import moa.announcement.query.response.AnnouncementResponse;
import moa.auth.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcements")
public class AnnouncementController implements AnnouncementApi {

    private final AnnouncementQueryService announcementQueryService;

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> findAll(
            @Auth(permit = SIGNED_UP) Long memberId
    ) {
        List<AnnouncementResponse> result = announcementQueryService.findAll();
        return ResponseEntity.ok(result);
    }
}

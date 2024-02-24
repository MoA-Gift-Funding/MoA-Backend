package moa.cs;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.cs.application.PersonalInquiryService;
import moa.cs.query.PersonalInquireQueryService;
import moa.cs.query.response.PersonalInquiryResponse;
import moa.cs.request.WriteInquiryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/personal-inquiries")
public class PersonalInquiryController implements PersonalInquiryApi {

    private final PersonalInquiryService personalInquiryService;
    private final PersonalInquireQueryService personalInquireQueryService;

    @PostMapping
    public ResponseEntity<Void> inquire(
            @Auth(permit = SIGNED_UP) Long memberId,
            @Valid @RequestBody WriteInquiryRequest request
    ) {
        Long inquiryId = personalInquiryService.inquire(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/personal-inquiries/" + inquiryId)).build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<PersonalInquiryResponse>> findMy(
            @Auth(permit = SIGNED_UP) Long memberId
    ) {
        var result = personalInquireQueryService.findByMemberId(memberId);
        return ResponseEntity.ok(result);
    }
}

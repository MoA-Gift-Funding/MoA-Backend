package moa.customerservicecenter;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.customerservicecenter.query.FAQQueryService;
import moa.customerservicecenter.query.response.FAQResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faqs")
public class FAQController implements FAQControllerApi {

    private final FAQQueryService faqQueryService;

    @GetMapping
    public ResponseEntity<List<FAQResponse>> findMy(
            @Auth(permit = SIGNED_UP) Long memberId
    ) {
        var result = faqQueryService.findAll();
        return ResponseEntity.ok(result);
    }
}

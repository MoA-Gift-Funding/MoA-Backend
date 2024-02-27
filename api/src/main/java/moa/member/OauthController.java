package moa.member;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.global.jwt.JwtResponse;
import moa.global.jwt.JwtService;
import moa.member.application.OauthService;
import moa.member.domain.OauthId.OauthProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController implements OAuthApi {

    private final OauthService oauthService;
    private final JwtService jwtService;

    @GetMapping("/login/app/{oauthProvider}")
    public ResponseEntity<JwtResponse> login(
            @PathVariable("oauthProvider") OauthProvider oauthProvider,
            @RequestHeader(name = "OAuthAccessToken") String oauthAccessToken
    ) {
        Long memberId = oauthService.login(oauthProvider, oauthAccessToken);
        String accessToken = jwtService.createAccessToken(memberId);
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @Auth(permit = {SIGNED_UP}) Long memberId
    ) {
        oauthService.withdraw(memberId);
        return ResponseEntity.noContent().build();
    }
}

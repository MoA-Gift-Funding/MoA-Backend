package moa.member;

import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.member.application.MemberService;
import moa.member.query.MemberQueryService;
import moa.member.query.response.MemberResponse;
import moa.member.query.response.NotificationStatusResponse;
import moa.member.request.MemberUpdateRequest;
import moa.member.request.NotificationPermitRequest;
import moa.member.request.SendPhoneVerificationNumberRequest;
import moa.member.request.SignupRequest;
import moa.member.request.VerifyPhoneRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberApi {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;

    @GetMapping("/my")
    public ResponseEntity<MemberResponse> findMyProfile(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId
    ) {
        MemberResponse response = memberQueryService.findMyProfile(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verification/phone/send-number")
    public ResponseEntity<Void> sendPhoneVerificationNumber(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId,
            @Valid @RequestBody SendPhoneVerificationNumberRequest request
    ) {
        memberService.sendPhoneVerificationNumber(memberId, request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verification/phone/verify")
    public ResponseEntity<Void> verifyPhone(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId,
            @Valid @RequestBody VerifyPhoneRequest request
    ) {
        memberService.verifyPhone(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> signup(
            @Auth(permit = {PRESIGNED_UP}) Long memberId,
            @Valid @RequestBody SignupRequest request
    ) {
        memberService.signup(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notification")
    public ResponseEntity<NotificationStatusResponse> checkNotificationStatus(
            @Auth(permit = {SIGNED_UP}) Long memberId
    ) {
        return ResponseEntity.ok(memberQueryService.checkNotification(memberId));
    }

    @PostMapping("/notification")
    public ResponseEntity<Void> permitNotification(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody NotificationPermitRequest request
    ) {
        memberService.permitNotification(memberId, request.deviceToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notification")
    public ResponseEntity<Void> rejectNotification(
            @Auth(permit = {SIGNED_UP}) Long memberId
    ) {
        memberService.rejectNotification(memberId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        memberService.update(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }
}

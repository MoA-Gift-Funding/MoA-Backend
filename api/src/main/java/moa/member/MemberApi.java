package moa.member;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;
import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import moa.auth.Auth;
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
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "회원 API", description = "회원 관련 API")
@SecurityRequirement(name = "JWT")
public interface MemberApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "회원 프로필 조회")
    @GetMapping("/my")
    ResponseEntity<MemberResponse> findMyProfile(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "409", description = "해당 번호를 사용하는 다른 사용자가 이미 존재하는 경우"),
            }
    )
    @Operation(summary = "핸드폰 인증번호 전송")
    @PostMapping("/verification/phone/send-number")
    ResponseEntity<Void> sendPhoneVerificationNumber(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId,

            @Valid @RequestBody SendPhoneVerificationNumberRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "409", description = "해당 번호를 사용하는 다른 사용자가 이미 존재하는 경우"),
            }
    )
    @Operation(summary = "핸드폰 인증번호 확인")
    @PostMapping("/verification/phone/verify")
    ResponseEntity<Void> verifyPhone(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId,

            @Valid @RequestBody VerifyPhoneRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "회원가입")
    @PostMapping
    ResponseEntity<Void> signup(
            @Auth(permit = {PRESIGNED_UP}) Long memberId,

            @Valid @RequestBody SignupRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "푸쉬알림 동의 여부 조회")
    @GetMapping("/notification")
    ResponseEntity<NotificationStatusResponse> checkNotificationStatus(
            @Auth(permit = {SIGNED_UP}) Long memberId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "푸쉬알림 동의")
    @PostMapping("/notification")
    ResponseEntity<Void> permitNotification(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Valid @RequestBody NotificationPermitRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "푸쉬알림 거절")
    @DeleteMapping("/notification")
    ResponseEntity<Void> rejectNotification(
            @Auth(permit = {SIGNED_UP}) Long memberId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "회원정보 수정")
    @PutMapping
    ResponseEntity<Void> update(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Valid @RequestBody MemberUpdateRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    ResponseEntity<Void> withdraw(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(
                    description = "가입한 OAuth 서비스에서 받아온 AccessToken (Apple의 경우에는 AuthCode)",
                    in = HEADER,
                    required = true
            )
            @RequestHeader(name = "OAuthAccessToken") String oauthAccessToken
    );
}

package moa.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import moa.auth.Auth;
import moa.member.presentation.request.MemberUpdateRequest;
import moa.member.presentation.request.SendPhoneVerificationNumberRequest;
import moa.member.presentation.request.SignupRequest;
import moa.member.presentation.request.VerifyPhoneRequest;
import moa.member.query.response.MemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "회원 API", description = "회원 관련 API")
@SecurityRequirement(name = "JWT")
public interface MemberApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
            }
    )
    @Operation(summary = "회원 프로필 조회")
    @GetMapping("/my")
    ResponseEntity<MemberResponse> findMyProfile(
            @Parameter(hidden = true) @Auth Long memberId
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
            @Parameter(hidden = true) @Auth Long memberId,
            @Schema SendPhoneVerificationNumberRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "핸드폰 검증이 이루어지지 않은 경우"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "409", description = "해당 번호를 사용하는 다른 사용자가 이미 존재하는 경우"),
            }
    )
    @Operation(summary = "핸드폰 인증번호 확인")
    @PostMapping("/verification/phone/verify")
    ResponseEntity<Void> verifyPhone(
            @Parameter(hidden = true) @Auth Long memberId,
            @Schema VerifyPhoneRequest request
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
            @Parameter(hidden = true) @Auth Long memberId,
            @Schema SignupRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "회원가입되지 않은 회원의 경우(임시 회원가입인 경우도 해당 케이스)"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "회원정보 수정")
    @PutMapping
    ResponseEntity<Void> update(
            @Parameter(hidden = true) @Auth Long memberId,
            @Schema MemberUpdateRequest request
    );
}

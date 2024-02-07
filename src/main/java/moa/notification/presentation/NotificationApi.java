package moa.notification.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import moa.auth.Auth;
import moa.notification.query.response.CheckExistsUnreadNotificationResponse;
import moa.notification.query.response.NotificationResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "회원 API", description = "회원 관련 API")
@SecurityRequirement(name = "JWT")
public interface NotificationApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "회원가입되지 않은 회원의 경우(임시 회원가입인 경우도 해당 케이스)"
                    ),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "모든 알림을 조회한다", description = "알림을 조회하면 모든 알림이 읽음 처리된다.")
    @GetMapping
    ResponseEntity<List<NotificationResponses>> readAll(
            @Auth(permit = SIGNED_UP) Long memberId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "회원가입되지 않은 회원의 경우(임시 회원가입인 경우도 해당 케이스)"
                    ),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "읽지 않은 알림이 있는지 확인")
    @GetMapping("/check")
    ResponseEntity<CheckExistsUnreadNotificationResponse> existsUnread(
            @Auth(permit = SIGNED_UP) Long memberId
    );
}

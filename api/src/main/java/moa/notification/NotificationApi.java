package moa.notification;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import moa.auth.Auth;
import moa.notification.query.response.CheckExistsUnreadNotificationResponse;
import moa.notification.query.response.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "알림 API", description = "알림 관련 API")
@SecurityRequirement(name = "JWT")
public interface NotificationApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "모든 알림을 조회한다", description = "알림을 조회하면 모든 알림이 읽음 처리된다.")
    @GetMapping
    ResponseEntity<List<NotificationResponse>> readAll(
            @Auth(permit = SIGNED_UP) Long memberId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "읽지 않은 알림이 있는지 확인")
    @GetMapping("/check")
    ResponseEntity<CheckExistsUnreadNotificationResponse> existsUnread(
            @Auth(permit = SIGNED_UP) Long memberId
    );
}

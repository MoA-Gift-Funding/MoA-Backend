package moa.friend.presentation;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import moa.auth.Auth;
import moa.friend.presentation.request.SyncContactRequest;
import moa.friend.query.response.FriendResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "친구 API", description = "친구 관련 API")
@SecurityRequirement(name = "JWT")
public interface FriendApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "연락처 동기화")
    @PostMapping("/sync-contact")
    ResponseEntity<Void> syncContact(
            @Parameter(hidden = true) @Auth(permit = {SIGNED_UP}) Long memberId,
            @Schema SyncContactRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "내 친구 목록 조회")
    @GetMapping("/my")
    ResponseEntity<List<FriendResponse>> findMyFriends(
            @Parameter(hidden = true) @Auth(permit = {SIGNED_UP}) Long memberId
    );
}

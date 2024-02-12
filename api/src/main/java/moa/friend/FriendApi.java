package moa.friend;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import moa.auth.Auth;
import moa.friend.query.response.FriendResponse;
import moa.friend.request.SyncContactRequest;
import moa.friend.request.UpdateFriendRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "친구 API", description = "친구 관련 API")
@SecurityRequirement(name = "JWT")
public interface FriendApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "연락처 동기화")
    @PostMapping("/sync-contact")
    ResponseEntity<Void> syncContact(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Schema
            @RequestBody SyncContactRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "주어진 친구 id가 내 친구 id가 아닌 경우"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "친구 정보 수정")
    @PutMapping("/{id}")
    ResponseEntity<Void> update(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(description = "친구 id (친구의 회원 id가 아니라 '친구' id)", in = ParameterIn.PATH, required = true)
            @PathVariable("id") Long friendId,

            @Schema
            @RequestBody UpdateFriendRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "주어진 친구 id가 내 친구 id가 아닌 경우"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "친구 차단")
    @PostMapping("/block/{id}")
    ResponseEntity<Void> block(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(description = "친구 id (친구의 회원 id가 아니라 '친구' id)", in = ParameterIn.PATH, required = true)
            @PathVariable("id") Long friendId
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "주어진 친구 id가 내 친구 id가 아닌 경우"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "친구 차단 해제")
    @PostMapping("/unblock/{id}")
    ResponseEntity<Void> unblock(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(description = "친구 id (친구의 회원 id가 아니라 '친구' id)", in = ParameterIn.PATH, required = true)
            @PathVariable("id") Long friendId
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
    @Operation(summary = "내 친구 목록 조회")
    @GetMapping
    ResponseEntity<List<FriendResponse>> findMyFriends(
            @Auth(permit = {SIGNED_UP}) Long memberId
    );
}

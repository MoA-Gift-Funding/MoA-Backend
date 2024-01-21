package moa.friend.presentation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import moa.friend.presentation.request.SyncContactRequest;
import moa.global.auth.Auth;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/sync-contact")
    ResponseEntity<Void> syncContact(
            @Parameter(hidden = true) @Auth Long memberId,
            @Schema SyncContactRequest request
    );
}

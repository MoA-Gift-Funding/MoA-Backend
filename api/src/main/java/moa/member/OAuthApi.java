package moa.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import moa.global.jwt.JwtResponse;
import moa.member.domain.OauthId.OauthProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "OAuth API", description = "OAuth 관련 API")
public interface OAuthApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            content = @Content(schema = @Schema(hidden = true)),
                            description = "받아온 email 로 이미 가입된 회원이 있는 경우"
                    )
            }
    )
    @Operation(summary = "Oauth 를 통해 로그인 진행 (최초 요청 시 임시가입)")
    @GetMapping("/login/app/{oauthProvider}")
    ResponseEntity<JwtResponse> login(
            @Parameter(description = "OAuth2.0 제공 서비스", in = ParameterIn.PATH, required = true)
            @PathVariable("oauthProvider") OauthProvider oauthProvider,

            @Parameter(
                    description = "해당 서비스에서 받아온 AccessToken (Apple의 경우에는 AuthCode)",
                    in = ParameterIn.HEADER,
                    required = true
            )
            @RequestHeader(name = "OAuthAccessToken") String oauthAccessToken
    );
}

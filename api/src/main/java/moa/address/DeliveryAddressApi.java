package moa.address;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import moa.address.query.response.AddressResponse;
import moa.address.request.AddressCreateRequest;
import moa.address.request.AddressUpdateRequest;
import moa.auth.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "배송지 API", description = "배송지 관련 API")
@SecurityRequirement(name = "JWT")
public interface DeliveryAddressApi {

    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            headers = {@Header(name = "Location", description = "/addresses/{id} 형식")}
                    ),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "배송지 생성")
    @PostMapping
    ResponseEntity<Void> create(
            @Auth(permit = SIGNED_UP) Long memberId,

            @Valid @RequestBody AddressCreateRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "자신의 주소지가 아닌 경우"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "배송지 수정")
    @PutMapping("/{id}")
    ResponseEntity<Void> update(
            @Auth(permit = SIGNED_UP) Long memberId,

            @Parameter(description = "배송지 id", in = ParameterIn.PATH, required = true)
            @PathVariable("id") Long id,

            @RequestBody AddressUpdateRequest request
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403", description = "자신의 주소지가 아닌 경우"),
                    @ApiResponse(responseCode = "404"),
            }
    )
    @Operation(summary = "배송지 제거")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @Auth(permit = SIGNED_UP) Long memberId,

            @Parameter(description = "배송지 id", in = ParameterIn.PATH, required = true)
            @PathVariable("id") Long id
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
    @Operation(summary = "내 배송지 목록 조회")
    @GetMapping
    ResponseEntity<List<AddressResponse>> findMy(
            @Auth(permit = SIGNED_UP) Long memberId
    );
}

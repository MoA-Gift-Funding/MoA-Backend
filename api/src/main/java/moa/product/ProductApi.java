package moa.product;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.springframework.data.domain.Sort.Direction.ASC;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import moa.auth.Auth;
import moa.global.presentation.PageResponse;
import moa.product.query.response.ProductDetailResponse;
import moa.product.query.response.ProductResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "상품 API", description = "상품 관련 API")
@SecurityRequirement(name = "JWT")
public interface ProductApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
            }
    )
    @Operation(summary = "판매 중인 상품 조회 (판매 종료일까지 5주 이상 남은 상품 조회)")
    @GetMapping
    ResponseEntity<PageResponse<ProductResponse>> findAllOnSale(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = QUERY, required = false, description = "카테고리 이름")
            @RequestParam(value = "category", required = false) String category,

            @ParameterObject
            @PageableDefault(size = 10, sort = "id", direction = ASC) Pageable pageable
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
    @Operation(summary = "특정 상품 상세 조회")
    @GetMapping("/{productId}")
    ResponseEntity<ProductDetailResponse> findById(
            @Auth(permit = {SIGNED_UP}) Long memberId,

            @Parameter(in = PATH, required = true, description = "상품 ID")
            @PathVariable("productId") Long productId
    );
}

package moa.product;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.springframework.data.domain.Sort.Direction.ASC;

import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.global.presentation.PageResponse;
import moa.product.query.ProductQueryService;
import moa.product.query.response.ProductDetailResponse;
import moa.product.query.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController implements ProductApi {

    private final ProductQueryService productQueryService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAllOnSale(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @RequestParam(value = "category", required = false) String category,
            @PageableDefault(size = 10, sort = "id", direction = ASC) Pageable pageable
    ) {
        if (ObjectUtils.isEmpty(category)) {
            Page<ProductResponse> result = productQueryService.findAllOnSale(pageable);
            return ResponseEntity.ok(PageResponse.from(result));
        }
        Page<ProductResponse> result = productQueryService.findAllOnSaleByCategory(category, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> findById(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @PathVariable("productId") Long productId
    ) {
        ProductDetailResponse response = productQueryService.findById(productId);
        return ResponseEntity.ok(response);
    }
}

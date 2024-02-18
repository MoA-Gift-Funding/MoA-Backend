package moa.product.dev;

import static moa.product.domain.ProductId.ProductProvider.WINCUBE;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import moa.global.domain.Price;
import moa.product.domain.Product;
import moa.product.domain.ProductId;
import moa.product.domain.ProductRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/products")
public class DevProductController {

    private final ProductRepository productRepository;

    @PostMapping
    public void create(@RequestBody ProductCreateReuqest reuqest) {
        String string = UUID.randomUUID().toString();
        productRepository.save(new Product(
                new ProductId(string, WINCUBE),
                "imageUrl",
                "brand",
                "category",
                reuqest.name,
                Price.from(reuqest.price),
                "desc",
                LocalDate.now().plusDays(1000),
                0,
                60
        ));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<Product> all = productRepository.findAll();
        List<ProductResponse> list = all.stream()
                .map(it -> new ProductResponse(it.getId(), it.getProductName(), it.getPrice().longValue()))
                .toList();
        return ResponseEntity.ok(list);
    }

    record ProductCreateReuqest(
            String name,
            String price
    ) {
    }

    record ProductResponse(
            Long id,
            String name,
            Long price
    ) {
    }
}

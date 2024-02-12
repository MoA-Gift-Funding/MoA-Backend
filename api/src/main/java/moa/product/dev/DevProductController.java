package moa.product.dev;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.global.domain.Price;
import moa.product.domain.Product;
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
        productRepository.save(new Product(reuqest.name, Price.from(reuqest.price)));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<Product> all = productRepository.findAll();
        List<ProductResponse> list = all.stream()
                .map(it -> new ProductResponse(it.getId(), it.getName(), it.getPrice().longValue()))
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

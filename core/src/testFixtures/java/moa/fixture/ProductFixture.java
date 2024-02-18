package moa.fixture;

import static moa.product.domain.ProductId.ProductProvider.WINCUBE;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import moa.global.domain.Price;
import moa.product.domain.Product;
import moa.product.domain.ProductId;

public class ProductFixture {

    public static Product product(String name, Price price) {
        String string = UUID.randomUUID().toString();
        return new Product(
                new ProductId(string, WINCUBE),
                "imageUrl",
                "brand",
                "category",
                name,
                price,
                "desc",
                LocalDate.now().plusDays(1000),
                0,
                60,
                List.of()
        );
    }
}

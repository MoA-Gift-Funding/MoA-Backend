package moa.dev;

import static moa.product.domain.ProductId.ProductProvider.WINCUBE;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.global.domain.Price;
import moa.product.domain.Product;
import moa.product.domain.ProductId;
import moa.product.domain.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ProductMockDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        productRepository.saveAll(List.of(
                new Product(
                        new ProductId("1", WINCUBE),
                        "https://image6.coupangcdn.com/image/retail/images/218005008681-0735e1ea-25ea-4d80-b172-74d4cfee0812.jpg",
                        "Apple",
                        "전자제품",
                        "애플 에어팟 맥스 실버",
                        Price.from("100000"),
                        "애플 에어팟 맥스 실버 사주세요 루마...",
                        LocalDate.now().plusYears(20),
                        0,
                        60
                ),
                new Product(
                        new ProductId("2", WINCUBE),
                        "https://image7.coupangcdn.com/image/retail/images/2023/10/24/18/3/f8922ef4-2832-4aee-a2d6-541f599049be.jpg",
                        "UGG",
                        "신발",
                        "어그 디스케트 슬리퍼 체스트넛",
                        Price.from("50000"),
                        "이건 저는 괜찮습니다 루마...",
                        LocalDate.now().plusYears(20),
                        10,
                        80
                ),
                new Product(
                        new ProductId("3", WINCUBE),
                        "https://d3vfig6e0r0snz.cloudfront.net/rcYjnYuenaTH5vyDF/images/products/c7d42ebb49f60f82677292b716e4445b.webp",
                        "The North Face",
                        "의류",
                        "노스페이스 1996 에코 눕시 자켓 블랙",
                        Price.from("30000"),
                        "동쪽 얼굴",
                        LocalDate.now().plusYears(10),
                        0,
                        10
                ),
                new Product(
                        new ProductId("4", WINCUBE),
                        "https://d3vfig6e0r0snz.cloudfront.net/rcYjnYuenaTH5vyDF/images/products/c7d42ebb49f60f82677292b716e4445b.webp",
                        "Nike",
                        "신발",
                        "나이키 에어포스 1 '07 WB 플랙스",
                        Price.from("10000"),
                        "나이키 신발도 좋아요",
                        LocalDate.now().plusYears(20),
                        20,
                        60
                ),
                new Product(
                        new ProductId("5", WINCUBE),
                        "https://cdn.011st.com/11dims/resize/600x600/quality/75/11src/product/1471984330/B.jpg?354000000",
                        "GS25",
                        "편의점",
                        "GS 모바일상품권 5천원권",
                        Price.from("5000"),
                        "GS 상품권임",
                        LocalDate.now().plusYears(20),
                        0,
                        60
                )
        ));
    }
}

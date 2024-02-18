package moa.product.client;

import lombok.RequiredArgsConstructor;
import moa.product.client.dto.WincubeProductResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WincubeClient {

    private final WincubeProperty wincubeProperty;
    private final WincubeApiClient client;

    public WincubeProductResponse getProductList() {
        return client.getProductList(wincubeProperty.mdCode(), "JSON", wincubeProperty.token());
    }
}

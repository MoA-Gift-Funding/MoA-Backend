package moa.product.client.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WincubeAuthClient {

    private final WincubeAuthProperty wincubeProperty;
    private final WincubeAuthApiClient authClient;
    private final Aes256 aes256;
    private final Rsa rsa;


}

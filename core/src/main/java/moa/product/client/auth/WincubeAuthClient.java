package moa.product.client.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WincubeAuthClient {

    private final WincubeAuthProperty wincubeProperty;
    private final WincubeAuthApiClient authClient;

//    public WincubeAuthResponse getAuth() {
//        Aes256 aes256 = new Aes256();
//        return authClient.getAuthToken(
//                aes256.aes256Enc(wincubeProperty.custId(), ),
//                wincubeProperty.pwd(),
//                wincubeProperty.autKey(),
//                wincubeProperty.aesKey(),
//                wincubeProperty.aesIv()
//        );
//    }
}

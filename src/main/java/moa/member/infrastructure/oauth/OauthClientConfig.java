package moa.member.infrastructure.oauth;

import lombok.RequiredArgsConstructor;
import moa.global.http.HttpInterfaceUtil;
import moa.member.infrastructure.oauth.apple.AppleApiClient;
import moa.member.infrastructure.oauth.kakao.KakaoApiClient;
import moa.member.infrastructure.oauth.naver.NaverApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
public class OauthClientConfig {

    private final RestClient restClient;

    @Bean
    public KakaoApiClient kakaoApiClient() {
        return HttpInterfaceUtil.createHttpInterface(restClient, KakaoApiClient.class);
    }

    @Bean
    public NaverApiClient naverApiClient() {
        return HttpInterfaceUtil.createHttpInterface(restClient, NaverApiClient.class);
    }

    @Bean
    public AppleApiClient appleApiClient() {
        return HttpInterfaceUtil.createHttpInterface(restClient, AppleApiClient.class);
    }
}

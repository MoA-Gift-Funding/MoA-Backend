package moa.member.domain;

import static jakarta.persistence.EnumType.STRING;
import static java.util.Locale.ENGLISH;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class OauthId {

    @Column(nullable = true, name = "oauth_id")
    private String oauthId;  // 각 Oauth 서비스에서 회원을 식별하는 id

    @Enumerated(STRING)
    @Column(nullable = true, name = "oauth_provider")
    private OauthProvider oauthProvider;

    public OauthId(String oauthId, OauthProvider oauthProvider) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public enum OauthProvider {
        KAKAO,
        NAVER,
        APPLE;

        public static OauthProvider fromName(String provider) {
            return OauthProvider.valueOf(provider.toUpperCase(ENGLISH));
        }
    }
}

package moa.member.infrastructure.oauth.apple;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static moa.member.domain.OauthId.OauthProvider.APPLE;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.global.jwt.JwtService;
import moa.member.domain.Member;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClient;
import moa.member.infrastructure.oauth.apple.response.AppleTokenResponse;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleMemberClient implements OauthMemberClient {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String AUDIENCE = "https://appleid.apple.com";

    private final AppleOauthConfig appleOauthConfig;
    private final AppleApiClient appleApiClient;
    private final JwtService jwtService;

    @Override
    public OauthProvider supportsProvider() {
        return APPLE;
    }

    @Override
    public Member fetch(String authCode) {
        AppleTokenResponse response = appleApiClient.fetchToken(
                appleOauthConfig.clientId(),
                generateClientSecret(),
                authCode,
                GRANT_TYPE
        );
        String idToken = response.idToken();
        AppleIdTokenPayload appleIdTokenPayload = jwtService.decodePayload(idToken, AppleIdTokenPayload.class);
        log.info("애플 로그인 성공: {}", appleIdTokenPayload);
        return appleIdTokenPayload.toMember();
    }

    private String generateClientSecret() {
        long expiration = MILLISECONDS.convert(5, MINUTES);

        // https://developer.apple.com/documentation/accountorganizationaldatasharing/creating-a-client-secret
        return Jwts.builder()
                .setHeaderParam("kid", appleOauthConfig.keyId())
                .setIssuer(appleOauthConfig.teamId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiration + currentTimeMillis()))
                .setAudience(AUDIENCE)
                .setSubject(appleOauthConfig.clientId())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource(appleOauthConfig.privateKeyFileName());
            String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            Reader pemReader = new StringReader(privateKey);
            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            return converter.getPrivateKey(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

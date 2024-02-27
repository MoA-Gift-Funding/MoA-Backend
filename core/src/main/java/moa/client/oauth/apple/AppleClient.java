package moa.client.oauth.apple;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

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
import moa.client.oauth.apple.response.AppleIdTokenPayload;
import moa.client.oauth.apple.response.AppleTokenResponse;
import moa.global.jwt.JwtService;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleClient {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String AUDIENCE = "https://appleid.apple.com";

    private final AppleOauthProperty appleOauthProperty;
    private final AppleApiClient appleApiClient;
    private final JwtService jwtService;

    public AppleIdTokenPayload getIdTokenPayload(String authCode) {
        AppleTokenResponse response = fetchToken(authCode);
        String idToken = response.idToken();
        AppleIdTokenPayload appleIdTokenPayload = jwtService.decodePayload(idToken, AppleIdTokenPayload.class);
        log.info("애플 회원 정보 조회 성공: {}", appleIdTokenPayload);
        return appleIdTokenPayload;
    }

    public void withdraw(String authCode) {
        AppleTokenResponse tokenResponse = fetchToken(authCode);
        appleApiClient.withdraw(
                appleOauthProperty.clientId(),
                generateClientSecret(),
                tokenResponse.accessToken(),
                "access_token"
        );
        log.info("애플 회원 탈퇴 성공: {}", authCode);
    }

    private AppleTokenResponse fetchToken(String authCode) {
        return appleApiClient.fetchToken(
                appleOauthProperty.clientId(),
                generateClientSecret(),
                authCode,
                GRANT_TYPE
        );
    }

    private String generateClientSecret() {
        long expiration = MILLISECONDS.convert(5, MINUTES);

        // https://developer.apple.com/documentation/accountorganizationaldatasharing/creating-a-client-secret
        return Jwts.builder()
                .setHeaderParam("kid", appleOauthProperty.keyId())
                .setIssuer(appleOauthProperty.teamId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiration + currentTimeMillis()))
                .setAudience(AUDIENCE)
                .setSubject(appleOauthProperty.clientId())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource(appleOauthProperty.privateKeyFileName());
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

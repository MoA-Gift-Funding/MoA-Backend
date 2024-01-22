package moa.global.jwt;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static moa.auth.AuthExceptionType.INVALID_TOKEN;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Date;
import moa.auth.AuthException;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String MEMBER_ID_CLAIM = "memberId";

    private final ObjectMapper objectMapper;
    private final Algorithm algorithm;
    private final long accessTokenExpirationPeriodDayToMills;

    public JwtService(JwtProperties jwtProperties) {
        this.objectMapper = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.algorithm = Algorithm.HMAC512(jwtProperties.secretKey());
        this.accessTokenExpirationPeriodDayToMills =
                MILLISECONDS.convert(jwtProperties.accessTokenExpirationPeriodDay(), DAYS);
    }

    public String createAccessToken(Long memberId) {
        return JWT.create()
                .withExpiresAt(
                        new Date(accessTokenExpirationPeriodDayToMills + currentTimeMillis())
                )
                .withClaim(MEMBER_ID_CLAIM, memberId)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public Long extractMemberId(String accessToken) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(accessToken)
                    .getClaim(MEMBER_ID_CLAIM)
                    .asLong();
        } catch (JWTVerificationException e) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    public <T> T decodePayload(String token, Class<T> targetClass) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String encodedPayload = JWT.decode(token).getPayload();
        String payload = new String(decoder.decode(encodedPayload));
        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }
}

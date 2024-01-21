package moa.member.infrastructure.redis;

import static java.util.concurrent.TimeUnit.MINUTES;

import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPhoneVerificationNumberRepository implements PhoneVerificationNumberRepository {

    private static final String PREFIX = "phoneVerification:";
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Member member, PhoneVerificationNumber phoneVerificationNumber) {
        redisTemplate.opsForValue().set(key(member), phoneVerificationNumber.value());
        redisTemplate.expire(key(member), VERIFICATION_NUMBER_TIMEOUT, MINUTES);
    }

    @Override
    public PhoneVerificationNumber getByMember(Member member) {
        String verificationNumber = redisTemplate.opsForValue().get(key(member));
        return new PhoneVerificationNumber(verificationNumber);
    }

    private String key(Member member) {
        return PREFIX + member.getId();
    }
}

package moa.member.infrastructure.redis;

import static java.util.concurrent.TimeUnit.MINUTES;

import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPhoneRepository implements PhoneRepository {

    private static final String PREFIX = "phone:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Phone phone) {
        Member member = phone.getMember();
        redisTemplate.opsForValue().set(key(member), phone.getPhoneNumber());
        redisTemplate.expire(key(member), PHONE_TIMEOUT, MINUTES);
    }

    @Override
    public Phone getByMember(Member member) {
        String phoneNumber = redisTemplate.opsForValue().get(key(member));
        return new Phone(member, phoneNumber);
    }

    private String key(Member member) {
        return PREFIX + member.getId();
    }
}

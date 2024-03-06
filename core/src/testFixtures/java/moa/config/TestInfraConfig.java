package moa.config;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import moa.client.toss.TossClient;
import moa.member.domain.Member;
import moa.member.domain.phone.Phone;
import moa.member.domain.phone.PhoneRepository;
import moa.member.domain.phone.PhoneVerificationNumber;
import moa.member.domain.phone.PhoneVerificationNumberRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestInfraConfig {

    @Bean
    public TossClient tossClient() {
        return mock(TossClient.class);
    }

    @Bean
    public PhoneVerificationNumberRepository phoneVerificationNumberRepository() {
        return new TestPhoneVerificationNumberRepository();
    }

    public static class TestPhoneVerificationNumberRepository implements PhoneVerificationNumberRepository {

        private final Map<Member, PhoneVerificationNumber> store = new HashMap<>();

        @Override
        public void save(Member member, PhoneVerificationNumber phoneVerificationNumber) {
            store.put(member, phoneVerificationNumber);
        }

        @Override
        public PhoneVerificationNumber getByMember(Member member) {
            return store.get(member);
        }
    }

    @Bean
    public PhoneRepository phoneRepository() {
        return new TestPhoneRepository();
    }

    public static class TestPhoneRepository implements PhoneRepository {

        private final Map<Member, Phone> store = new HashMap<>();

        @Override
        public void save(Phone phone) {
            store.put(phone.getMember(), phone);
        }

        @Override
        public Phone getByMember(Member member) {
            return store.get(member);
        }
    }
}

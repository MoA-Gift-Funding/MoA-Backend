package moa.member.domain.phone;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class VerificationNumberGenerator {

    public PhoneVerificationNumber generate() {
        int leftLimit = 48;  // numeral '0'
        int rightLimit = 57;  // numeral '9'
        int targetStringLength = 6;
        return new PhoneVerificationNumber(
                ThreadLocalRandom.current()
                        .ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
        );
    }
}

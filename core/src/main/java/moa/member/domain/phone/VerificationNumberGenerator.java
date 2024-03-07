package moa.member.domain.phone;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class VerificationNumberGenerator {

    public PhoneVerificationNumber generate() {
        int begin = 48;  // numeral '0'
        int end = 57;  // numeral '9'
        int targetStringLength = 6;
        return new PhoneVerificationNumber(
                ThreadLocalRandom.current()
                        .ints(begin, end + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
        );
    }
}

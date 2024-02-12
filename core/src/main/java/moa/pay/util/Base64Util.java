package moa.pay.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

import java.util.Base64;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class Base64Util {

    public static String parseToBase64(String str) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode(str.getBytes(UTF_8));
            return new String(encodedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package moa.client.wincube.auth;

import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Aes256Iv {

    public static String generateIv(int byteSize) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[byteSize / 2];  // 16진수로 암호화하는 과정에서 길이가 2배가 됨
        secureRandom.nextBytes(key);
        String iv = bytesToHex(key);
        log.debug("AES IV 생성 완료: {}", iv);
        return iv;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

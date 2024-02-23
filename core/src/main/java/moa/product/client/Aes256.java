package moa.product.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Aes256 {

    private static final String alg = "AES/CBC/PKCS5Padding";

    public String aes256Enc(String info, String aesKey, String aesIv) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance(alg);
            // 키로 비밀키 생성
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
            // iv로 spec 생성
            IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv.getBytes());
            // 암호화 적용
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            // 암호화 실행
            byte[] encrypted1 = cipher.doFinal(info.getBytes(UTF_8)); // 암호화 (인코딩 설정)

            result = Base64.getEncoder().encodeToString(encrypted1);
        } catch (Exception e) {
            log.error("암호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return result;
    }

    public String aes256Denc(String info, String aesKey, String aesIv) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            // 암호 해석
            byte[] decodedBytes = Base64.getDecoder().decode(info);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            result = new String(decrypted);
        } catch (Exception e) {
            log.error("암호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return result;
    }
}

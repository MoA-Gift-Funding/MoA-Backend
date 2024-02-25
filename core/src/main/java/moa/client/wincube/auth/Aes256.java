package moa.product.client.auth;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 윈큐브 AUTH 문서 - 3. AES256 클래스 코드
 */
@Slf4j
@Component
public class Aes256 {

    private static final String alg = "AES/CBC/PKCS5Padding";

    private final String aesKey;

    public Aes256(moa.product.client.auth.WincubeAuthProperty property) {
        this.aesKey = property.aesKey();
    }

    public String aes256Enc(String info, String aesIv) {
        // 암호화된 정보
        String result = null;

        // 알고리즘 aes-256 **********[암호화]**********
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

            // 암호화 인코딩 후 저장
            result = Base64.getEncoder().encodeToString(encrypted1);
        } catch (Exception e) {
            log.error("암호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return result;
    }

    public String aes256Denc(String info, String aesKey, String aesIv) {
        //----암호화 해석 코드 **********[복호화]**********
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
            log.error("복호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return result;
    }
}

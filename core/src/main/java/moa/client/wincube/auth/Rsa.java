package moa.client.wincube.auth;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 윈큐브 AUTH 문서 - 4. RSA 암호화 클래스 코드
 */
@Slf4j
@Component
public class Rsa {

    private static final int KEY_SIZE = 2048;

    private final String rsaPublicKey;

    public Rsa(WincubeAuthProperty property) {
        this.rsaPublicKey = property.rsaPublicKey();
    }

    /**
     * 키페어 생성
     */
    public Map<String, String> createKeypairAsString() {
        HashMap<String, String> stringKeypair = new HashMap<>();
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            stringKeypair.put("publicKey", stringPublicKey);
            stringKeypair.put("privateKey", stringPrivateKey);

        } catch (Exception e) {
            log.error("RSA 암호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return stringKeypair;
    }

    /**
     * 암호화
     */
    public String encode(String plainData) {
        String encryptedData = null;
        try {
            // 평문으로 전달받은 공개키를 공개키객체로 만드는 과정
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePublicKey = Base64.getDecoder().decode(rsaPublicKey.getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 만들어진 공개키 객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 평문을 암호화하는 과정
            byte[] byteEncryptedData = cipher.doFinal(plainData.getBytes());
            encryptedData = Base64.getEncoder().encodeToString(byteEncryptedData);
        } catch (Exception e) {
            log.error("RSA 암호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return encryptedData;
    }

    /**
     * 복호화
     */
    public String decode(String encryptedData, String stringPrivateKey) {
        String decryptedData = null;
        try {
            // 평문으로 전달받은 개인키를 개인키객체로 만드는 과정
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            // 만들어진 개인키 객체를 기반으로 복호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 암호문을 평문화하는 과정
            byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes());
            byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
            decryptedData = new String(byteDecryptedData);
        } catch (Exception e) {
            log.error("RSA 복호화 과정에서 문제가 발생했습니다. {}", e.getMessage());
        }
        return decryptedData;
    }
}

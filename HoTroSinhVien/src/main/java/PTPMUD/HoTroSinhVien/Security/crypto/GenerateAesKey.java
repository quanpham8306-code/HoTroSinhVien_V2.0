package PTPMUD.HoTroSinhVien.Security.crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerateAesKey {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        SecretKey secretKey = keyGenerator.generateKey();

        String key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println(key);
    }
}

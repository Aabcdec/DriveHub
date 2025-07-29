package com.example.beproject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;
import java.util.Random;

@SpringBootTest
class BeProjectApplicationTests {
    // 生成随机盐值
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new Random().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // 使用 SHA-256 + 盐值加密
    public static String encode(String rawPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashed = md.digest(rawPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 示例使用
    public static void main(String[] args) {
        String salt = "uEklWxHWMepj32U4A5UGkQ==";
        String password = "aaa111";
        String encoded = encode(password, salt);

        System.out.println("盐值: " + salt);
        System.out.println("加密后: " + encoded);

        // 验证时需使用相同盐值
        String inputPassword = "aaa111";
        String inputEncoded = encode(inputPassword, salt);
        System.out.println("验证结果: " + inputEncoded.equals(encoded));
    }
//    oS2NcMu/NX9/tw35sncrI/mfO+D0wfI9moYg3ASFXEw=
}

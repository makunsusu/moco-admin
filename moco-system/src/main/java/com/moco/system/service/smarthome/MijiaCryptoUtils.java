package com.moco.system.service.smarthome;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class MijiaCryptoUtils
{
    private static final String ALGORITHM = "AES";

    private static final String DEFAULT_INSECURE_TOKEN_SECRET = "change-me-before-production";

    private static final byte[] LEGACY_SECRET = "MocoMijiaKey2026".getBytes(StandardCharsets.UTF_8);

    private static volatile byte[] secret;

    public MijiaCryptoUtils()
    {
    }

    @Value("${moco.smarthome.crypto-secret:${MOCO_MIJIA_CRYPTO_SECRET:${token.secret:}}}")
    public void setSecret(String configuredSecret)
    {
        String normalized = configuredSecret == null ? "" : configuredSecret.trim();
        if (normalized.isEmpty() || DEFAULT_INSECURE_TOKEN_SECRET.equals(normalized))
        {
            secret = null;
            return;
        }
        secret = normalized.getBytes(StandardCharsets.UTF_8);
    }

    public static String encrypt(String plainText)
    {
        try
        {
            return encryptWithSecret(plainText, getSecret());
        }
        catch (Exception e)
        {
            throw new IllegalStateException("加密米家密码失败", e);
        }
    }

    public static String decrypt(String cipherText)
    {
        try
        {
            try
            {
                return decryptWithSecret(cipherText, getSecret());
            }
            catch (Exception primary)
            {
                return decryptWithSecret(cipherText, LEGACY_SECRET);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("解密米家密码失败", e);
        }
    }

    public static String md5Upper(String value)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : digest)
            {
                builder.append(String.format("%02X", b));
            }
            return builder.toString();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("计算米家密码摘要失败", e);
        }
    }

    private static byte[] getSecret()
    {
        byte[] current = secret;
        if (current == null || current.length == 0)
        {
            throw new IllegalStateException("请配置安全的米家密钥（moco.smarthome.crypto-secret / MOCO_MIJIA_CRYPTO_SECRET）");
        }
        return current;
    }

    private static String encryptWithSecret(String plainText, byte[] secretBytes) throws Exception
    {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretBytes, ALGORITHM));
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
    }

    private static String decryptWithSecret(String cipherText, byte[] secretBytes) throws Exception
    {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretBytes, ALGORITHM));
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
    }
}

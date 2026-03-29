package com.moco.system.service.smarthome;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class MijiaCryptoUtils
{
    private static final String ALGORITHM = "AES";

    private static final byte[] SECRET = "MocoMijiaKey2026".getBytes(StandardCharsets.UTF_8);

    private MijiaCryptoUtils()
    {
    }

    public static String encrypt(String plainText)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET, ALGORITHM));
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
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
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET, ALGORITHM));
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
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
}

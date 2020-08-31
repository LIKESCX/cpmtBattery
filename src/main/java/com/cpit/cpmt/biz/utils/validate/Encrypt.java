package com.cpit.cpmt.biz.utils.validate;


import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encrypt {

    public static String encrypt(String strKey, String v, String strIn) {
        try {
            SecretKeySpec skeySpec = getKey(strKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(v.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(strIn.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decrypt(String strKey, String v, String strIn) {
        try {
            SecretKeySpec skeySpec = getKey(strKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(v.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(strIn.getBytes());
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original,StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16];

        for (int i = 0; i < arrBTmp.length && i < arrB.length; ++i) {
            arrB[i] = arrBTmp[i];
        }

        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");
        return skeySpec;
    }

    public static String hmacMD5(String keyString, String msg) {
        String digest = null;

        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            byte[] bytes = mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            StringBuffer hash = new StringBuffer();

            for (int i = 0; i < bytes.length; ++i) {
                String hex = Integer.toHexString(255 & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }

                hash.append(hex);
            }

            digest = hash.toString().toUpperCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException  ex) {
            ;
        }

        return digest;
    }


}

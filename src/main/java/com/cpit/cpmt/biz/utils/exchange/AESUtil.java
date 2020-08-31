package com.cpit.cpmt.biz.utils.exchange;

import com.cpit.cpmt.biz.utils.validate.Encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 无论是请求数据还是返回的数据，都需要对data的内容进行AES对称加密
 * changed by zjg. The implementation is done in Encrypt class.
 * this class is redundant. To keep just for being compatible for code
 */
public class AESUtil {

    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);


    //加密
    /* str 源字符串
     * key 秘钥
     * iv 向量秘钥
     */
    public static String encrypt(String str, String key, String iv) {
        if (key == null) {
            logger.error("AES加密的key为空");
            return null;
        }
        // 判断Key是否为16位
        if (key.length() != 16) {
            logger.error("AES加密的key长度不是16位");
            return null;
        }
        return Encrypt.encrypt(key, iv, str);
    }

    // 解密
    /* sSrc 源字符串
     * sKey 秘钥
     * ivStr 向量秘钥
     */
    public static String decrypt(String sSrc, String sKey, String ivStr) {
        // 判断Key是否正确
        if (sKey == null) {
            logger.error("AES解密的key为空");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            logger.error("AES解密的key长度不是16位");
            return null;
        }

        return Encrypt.decrypt(sKey, ivStr, sSrc);

    }

    public static void main(String[] args) {
        String json = "{\"OperatorID\":\"565843400\",\"OperatorSecret\":\"575uFm7cbXNlaDQC\"}";
        String encode = encrypt(json, "bC8zTWAMuUkDv7fy", "uhhzaTYBxtoYkmR2");
        String decode = decrypt(encode,"bC8zTWAMuUkDv7fy", "uhhzaTYBxtoYkmR2");
        System.out.println(encode);
        System.out.println(decode);
        System.out.println(decode.equals(json));
    }

}

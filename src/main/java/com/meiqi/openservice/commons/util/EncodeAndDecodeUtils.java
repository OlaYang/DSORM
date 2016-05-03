package com.meiqi.openservice.commons.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class EncodeAndDecodeUtils {
    /**
     * 
     * @Title: encodeStr
     * @Description:base64 加密
     * @param @param plainText
     * @param @return
     * @return String
     * @throws
     */
    public static String encodeStrBase64(String str) {
        byte[] b = str.getBytes();
        Base64 base64 = new Base64();
        b = base64.encode(b);
        return new String(b);
    }



    /**
     * 
     * @Title: decodeStr
     * @Description:base64解密
     * @param @param str
     * @param @return
     * @return String
     * @throws
     */
    public static String decodeStrBase64(String str) {
        byte[] b = str.getBytes();
        Base64 base64 = new Base64();
        b = base64.decode(b);
        return new String(b);
    }



    /**
     * 
     * @Title: encodeStr
     * @Description:MD5 加密
     * @param @param plainText
     * @param @return
     * @return String
     * @throws
     */
    public static String encodeStrMD5(String str) {
        return DigestUtils.md5Hex(str);
    }

}

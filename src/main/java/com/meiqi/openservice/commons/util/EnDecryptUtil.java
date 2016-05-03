package com.meiqi.openservice.commons.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.meiqi.data.util.LogUtil;

import java.io.*;
import java.security.Key;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-4
 * Time: 下午5:11
 * DES对称加密工具
 */
public class EnDecryptUtil {
    private static char[] INT2CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static Map<Character, Integer> CHAR2INT = new HashMap<Character, Integer>();
    private static String keySpec = "abcdfslkfj9fflkdsflsfj230ru2390jljmeiqi888!@#";

    static {
        INT2CHAR.toString();
        for (int i = 0; i < INT2CHAR.length; i++) {
            CHAR2INT.put(INT2CHAR[i], i);
        }
    }

    public static String encrypt(String str) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(keySpec.getBytes());
        Key key = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return byte2string(cipher.doFinal(str.getBytes()));
    }

    public static String decrypt(String str) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(keySpec.getBytes());
        Key key = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return new String(cipher.doFinal(string2byte(str)));
    }

    private static String byte2string(byte[] buff) {
        char[] str = new char[buff.length * 2];

        for (int i = 0; i < buff.length; i++) {
            str[i * 2] = INT2CHAR[(buff[i] >>> 4) & 0xF];
            str[i * 2 + 1] = INT2CHAR[buff[i] & 0xF];
        }

        return new String(str);
    }

    private static byte[] string2byte(String string) {
        byte[] buff = new byte[string.length() / 2];
        char[] str = string.toCharArray();

        for (int i = 0; i < buff.length; i++) {
            buff[i] = (byte) ((CHAR2INT.get(str[i * 2]).intValue() << 4)
                    | (CHAR2INT.get(str[i * 2 + 1]).intValue()));
        }

        return buff;
    }

    /**
     * 摘要，默认大写
     *
     * @param source
     * @return
     */
    public static String digest(String source) {
        return digest(source, true);
    }

    /**
     * 摘要
     *
     * @param source  原文
     * @param isUpper 是否大写
     * @return
     */
    public static String digest(String source, boolean isUpper) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
        } catch (Exception e) {
            LogUtil.error("digest error, " + e.getMessage());
            return "";
        }
        md5.update(source.getBytes());
        return isUpper ? byte2string(md5.digest()) : byte2string(md5.digest()).toLowerCase();
    }

    // 加密
    public static String encode(Object source) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(os);
        outputStream.writeObject(source);
        outputStream.flush();
        os.flush();
        return byte2string(os.toByteArray());
    }

    // 解密
    public static Object decode(String source) throws IOException, ClassNotFoundException {
        return new ObjectInputStream(new ByteArrayInputStream(
                string2byte(source.trim().toUpperCase()))).readObject();

    }

}

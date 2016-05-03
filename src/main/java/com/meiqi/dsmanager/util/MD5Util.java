package com.meiqi.dsmanager.util;

import java.security.MessageDigest;
import java.util.UUID;

public class MD5Util {
	private static final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};  
	
	public static String md5encode(String param) {
		String uuid = UUID.nameUUIDFromBytes(param.getBytes()).toString();
		String[] ids = uuid.split("-");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			sb.append(ids[i].trim());
		}
		return sb.toString();
	}

	public static String UUID(String param) {
		String uuid = UUID.nameUUIDFromBytes(param.getBytes()).toString();
		String[] ids = uuid.split("-");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			sb.append(ids[i].trim());
		}
		return sb.toString();
	}
	
	public final static String MD5(String s) {
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}
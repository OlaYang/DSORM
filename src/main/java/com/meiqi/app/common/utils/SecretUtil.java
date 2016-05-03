package com.meiqi.app.common.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description:签名
 * Author: jiawen.huang
 * Date: 15/1/29
 * Time: 15:45
 * Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
public class SecretUtil {

	private static final String KEY_MD5 = "Akb7B9Nkx0a57d0J92Y8GI2817SQ76622TqT2Q1ntLQ9Vp93868c999pp55h";

	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String encryptMD5(String data) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			if (md5 != null) {
				md5.update(KEY_MD5.getBytes());
				return byteToString(md5.digest(data.getBytes()));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

}

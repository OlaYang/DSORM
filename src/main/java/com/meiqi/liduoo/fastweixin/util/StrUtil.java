package com.meiqi.liduoo.fastweixin.util;

import java.util.Random;

/**
 * 字符串常用方法工具类
 *
 * @author peiyu
 */
public final class StrUtil {

	/**
	 * 此类不需要实例化
	 */
	private StrUtil() {
	}

	/**
	 * 判断一个字符串是否为空，null也会返回true
	 *
	 * @param str
	 *            需要判断的字符串
	 * @return 是否为空，null也会返回true
	 */
	public static boolean isBlank(String str) {
		return null == str || "".equals(str.trim());
	}

	/**
	 * 判断一个字符串是否不为空
	 *
	 * @param str
	 *            需要判断的字符串
	 * @return 是否为空
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 判断一组字符串是否有空值
	 *
	 * @param strs
	 *            需要判断的一组字符串
	 * @return 判断结果，只要其中一个字符串为null或者为空，就返回true
	 */
	public static boolean hasBlank(String... strs) {
		if (null == strs || 0 == strs.length) {
			return true;
		} else {
			// 这种代码如果用java8就会很优雅了
			for (String str : strs) {
				if (isBlank(str)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getRandNum(int charCount) {
		String charValue = "";
		for (int i = 0; i < charCount; i++) {
			char c = (char) (randomInt(0, 10) + '0');
			charValue += String.valueOf(c);
		}
		return charValue;
	}

	public static int randomInt(int from, int to) {
		Random r = new Random();
		return from + r.nextInt(to - from);
	}

	public static boolean isNumber(String str) {
		if (str == null) {
			return false;
		}
		try {
			Long ll = Long.parseLong(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
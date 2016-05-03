package com.meiqi.liduoo.fastweixin.api;

import com.meiqi.liduoo.fastweixin.util.HttpKit;

/**
 * 红包API
 *
 * @author FrankGui
 */
public class RedPackAPI {
	// public RedPackAPI(ApiConfig config) {
	// super(config);
	// }

	public static String sendRedPack(String data, String keyStoreFile, String password) {
		String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
		String xmlStr = HttpKit.postSSL(url, data, keyStoreFile, password);
		return xmlStr;
	}

	public static String queryRedPack(String data, String keyStoreFile, String password) {
		String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";

		String xmlStr = HttpKit.postSSL(url, data, keyStoreFile, password);

		return xmlStr;
	}

	public static String sendToFans(String data, String keyStoreFile, String password) {
		String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

		String xmlStr = HttpKit.postSSL(url, data, keyStoreFile, password);

		return xmlStr;
	}

	public static String queryToFans(String data, String keyStoreFile, String password) {
		String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo";

		String xmlStr = HttpKit.postSSL(url, data, keyStoreFile, password);

		return xmlStr;
	}

}

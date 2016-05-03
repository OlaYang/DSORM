/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_PAY_;

import java.io.File;
import java.io.IOException;

import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;

/**
 * @author FrankGui 2015年12月24日
 */
public abstract class WeChatPayFunction extends WeChatFunction {

	protected String getKeyStoreFile(String keyStoreURL, String mch_id) throws IOException {
		File f = new File(keyStoreURL);
		if (f.exists()) {
			return keyStoreURL;
		}
		String cacheKey = keyStoreURL + "@@@" + mch_id;
		String localFile = (String) CacheUtils.getCache(cacheKey);
		if (localFile != null) {
			File tempFile = new File(localFile);
			if (tempFile.exists()) {
				return localFile;
			}
		}
		String url = CommonUtils.getWebImagePath(keyStoreURL);
		File file = File.createTempFile("key", ".store");
		CommonUtils.downloadToLocal(url, file);

		String fullPath = file.getAbsolutePath();
		CacheUtils.putCache(cacheKey, fullPath, CacheUtils.EXP_1_MONTH);

		return fullPath;
	}

}

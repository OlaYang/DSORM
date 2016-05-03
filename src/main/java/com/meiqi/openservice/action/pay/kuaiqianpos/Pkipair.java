package com.meiqi.openservice.action.pay.kuaiqianpos;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Pkipair {
	
	private static final Log LOG =  LogFactory.getLog("pay");
	
	@SuppressWarnings("restriction")
	public String signMsg( String signMsg) {

		String base64 = "";
		try {
			//密钥仓库
			KeyStore ks = KeyStore.getInstance("PKCS12");
			String fStr = Pkipair.class.getResource("81251004816010590.pfx").getPath();
			//fStr = fStr.substring(1);
			//读取密钥仓库
			FileInputStream ksfis = new FileInputStream(fStr);
			BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
			char[] keyPwd = "vpos123".toCharArray();
			ks.load(ksbufin, keyPwd);
			//从密钥仓库得到私钥
			PrivateKey priK = (PrivateKey) ks.getKey("81251004816010590", keyPwd);
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(priK);
			signature.update(signMsg.getBytes("UTF-8"));
			sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
			base64 = encoder.encode(signature.sign());
		} catch(FileNotFoundException e){
			System.out.println("文件找不到");
			LOG.error("文件找不到,"+e.getMessage());
		}catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage());
		}
		return base64;
	}
	
}

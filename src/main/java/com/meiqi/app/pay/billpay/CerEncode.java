package com.meiqi.app.pay.billpay;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CerEncode {
	
	
	@SuppressWarnings("restriction")
	public static boolean enCodeByCer(String val, String msg) {
		boolean flag = false;
		try {
			String fStr = CerEncode.class.getResource("config/mgw.cer").getPath();
			InputStream inStream = new FileInputStream(fStr);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
			PublicKey pk = cert.getPublicKey();
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(pk);
			signature.update(val.getBytes());
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			flag = signature.verify(decoder.decodeBuffer(msg));
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			System.out.println("文件找不到");
		}
		return flag;
	}
	
}

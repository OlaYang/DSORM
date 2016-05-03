package com.meiqi.openservice.action.pay.pc.alipay.config;

public interface AliPayConfig {
    
    String SELLER_EMAIL = "707428854@qq.com";
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串
    String PARTNER = "2088911500710372";
    // 商户的私钥, 如果签名方式设置为“0001”时，请设置该参数
    String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOLAyodRYpXkx0NAakKrCk+ja27INAD+Nd+YNPeeemiTJ6K2jGwqESoBobG5Gtlns9o5MLpvrlF464CWowwtcPZyrKyjUH8YL/eMvKSWbANjkMjbsS9yAL+5R2dkT7dyZ/i0vZru+DmUYRLlTIx8Aq9PO0HWMPRrgkDEgfOuXOTnAgMBAAECgYAByQrPLfDcxURcXzF2/OnD3sGFycW/DEFYVVZ1bKYCFMaHHXDVmS8xaXngcFipxYtA+JpNnXt94LnZ9VWrTuBIzKsRoxJWXW3Ld+oJPqHH2UeYp/yBNb//zmo+JjiX/d+8beZ40p29CaPYY2bpR66SiVqps5MZYao7NUPo2qUJ4QJBAPIPr4VX6KJIB3+7EeFvtuox1rVr2/rdpW0nQikPUuO4F6H2X8lHRsEdw+Y3rYxH9ce6WdaE2ln94On1N0mjqhMCQQDvz3Ej7+/g/VJtgAGB30j4p2ltIv7/d6n3wpgaI4MVxJDhtgEOl1JjsW8fLlS4PJnQVbdCv3jx9Q69OFXquvRdAkB/GsdywFdyp1nws11PQCcPUL5Ko2lukGZmK6AtCVPgKXGXZVSkz12S2DtersvdhofDqdG0uCwj5xDsKQwwCdWrAkEAk7hi51I7n4osJkt9ojSazKDDbnjqPw/FYVs7Oo65hsUowgDNMeHA3KxF2R0/DqY10YGbe8BajiD4TVLQieFT+QJAF7AhXfKmK7oeC4yXw9U5L6Wv6BYCtEihMLL6QX7SWoN5hZ/SklScdwczoa+a2gCTgLFM2Oe4k1Gsel0UqfsFpA==";
    // 支付宝的公钥， 如果签名方式设置为“0001”时，请设置该参数
    String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    
    
	// 支付宝账号
	String SELLER_EMAIL_NEW = "ytlj@lejj.com";
	String SIGN_TYPE = "RSA";
	// 字符编码格式 目前支持 utf-8
	String INPUT_CHARSET = "utf-8";
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	String PARTNER_NEW = "2088021700341695";
	// 商户的私钥, 如果签名方式设置为“0001”时，请设置该参数
	String PRIVATE_KEY_NEW = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPB8jXce+xomrAnBN/JGfs7pJ7IC7apsnkYZRTZrBnraNFRIP3fOdVxjjNt+/lTcKxMu+xlKIcgTTwm3qMvbxYnDv53r8SonojYPeSElcDeWFbP+9JjDCoinzbQFb4N7wWWPx+kmv0NjlrsnFY1k6FZzVKrrG67qqvMF4y04ysitAgMBAAECgYEAtEvNC5fQjiScKpwPw7YScDHhLtZ/NJxGdSuQ9mF69DQPs3iPDNVeq6t++TjWmOyP5sv8OVOYzBWd1h05kczs5Q4uzTxRAnGbxilKVegyAEs9JxZAz1ErYiqJEk2/5v4XqgYxBaOIygiSoedsDU9GlfZOvGAw4ltjl9ZxlEjOKYECQQD4gpFsdIdcnHEexzy+/DKLf8ZvXFYQVtVtFT2PPoHeiaNbUSUtABWkr6p2RgyEoorTFnTpEqxUB2fGtwRwTta9AkEA97wT2VoRNIy0kGgz5TKw7mFa3U0EN4ni3KCocchReguV6IDqfJ2LKy2pVwx680e3fSn93jP4jVVnnnMzzlaQsQJAa+UUiogVdSofGwPZ5fnNYC+70gG/BB0PUOsV7SlwbVRI7o7OmzkcLIJweiVzrep8Z6WqYL9QvrLwZaB0duZj6QJALsg9l1YC2cGYd4y2ABpKfzmzRwc8PKC1TqSoKpSBjmKRtI5juZSmq0i8KcwZi8eHRwOfBU8bKNVv8+QdbGG8gQJBAMKpQNhsqRtOA7u0wgQeEMoQvW6lkfHrBkLJeUG9nEoKNL/ITa5nX70eGBSMyTzSwsy3X/Vfm4yKnL69j6BJwIw=";
	// 支付宝的公钥， 如果签名方式设置为“0001”时，请设置该参数
    String PUBLIC_KEY_NEW  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    //分隔符
	String up="up";
	
	//pc端秘钥
	String PRIVATE_KEY_PC="tjc8hj0zbhsam3es2qo9dpuowugwb6e6";
	
	String SIGN_TYPE_PC="MD5";
	
	//梦百合商户号
	String PARTNER_MILLY = "2088801318993910";
	//梦百合pc端秘钥
	String PRIVATE_KEY_PC_MILLY="cimpzrwtyfccihgrf2w6r6q2lmbemkfp";
	//梦百合账号
	String SELLER_EMAIL_MILLY = "sunyaling@mlily.com";
	
	//海尔商户号
	String PARTNER_HAIER = "2088211472809365";
	//海尔pc端秘钥
	String PRIVATE_KEY_PC_HAIER="l09krg013ght1uw0x86omlv38u49dxu6";
	//海尔账号
	String SELLER_EMAIL_HAIER = "finance@avandeo.cn";
	
}

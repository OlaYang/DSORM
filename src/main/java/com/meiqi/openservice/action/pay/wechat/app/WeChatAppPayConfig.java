package com.meiqi.openservice.action.pay.wechat.app;

import java.util.Properties;

import com.meiqi.dsmanager.util.ConfigFileUtil;

public class WeChatAppPayConfig {
	
	//mdb APP
	public static String MDB_APP_APP_ID="wxc382f5aaa13e0c64"; //应用appid
	public static String MDB_APP_APP_KEY="f930be93c555193766cf66259c93d4ce";  //应用密钥
	public static String MDB_APP_MCH_ID="1307856601"; //商户号
	public static String MDB_APP_NOTIFY_URL="http://dsorm.lejj.com/pay/mdb/wechat/app/notify.do"; //支付异步通知地址
	
	//APP
	public static String APP_APP_ID="wx53bfbfc4ac8108f0"; //应用appid
	public static String APP_APP_KEY="B7A363C8676490C879BD8138FD9C5B4A";  //应用密钥
	public static String APP_MCH_ID="1302099101"; //商户号
	public static String APP_NOTIFY_URL="http://dsorm.lejj.com/pay/yjg/wechat/app/notify.do"; //支付异步通知地址
	
//	//安卓
//	public static String ANDROID_APP_ID="wx53bfbfc4ac8108f0"; //应用appid
//	public static String ANDROID_APP_KEY="B7A363C8676490C879BD8138FD9C5B4A";  //应用密钥
//	public static String ANDROID_MCH_ID="1302099101"; //商户号
//	public static String ANDROID_NOTIFY_URL="http://118.122.120.144:9876/DSORM/pay/yjg/wechat/app/android/notify.do"; //支付异步通知地址
//	//iphone 普通版本
//	public static String IPHONE_APP_ID="wx53bfbfc4ac8108f0"; //应用appid
//	public static String IPHONE_APP_KEY="B7A363C8676490C879BD8138FD9C5B4A";  //应用密钥
//	public static String IPHONE_MCH_ID="1302099101"; //商户号
//	public static String IPHONE_NOTIFY_URL="http://118.122.120.144:9876/DSORM/pay/yjg/wechat/app/iphone/notify.do"; //支付异步通知地址
//	//iphone 企业版本
//	public static String IPHONE_ENTERPRISE_APP_ID="wx53bfbfc4ac8108f0"; //应用appid
//	public static String IPHONE_ENTERPRISE_APP_KEY="B7A363C8676490C879BD8138FD9C5B4A";  //应用密钥
//	public static String IPHONE_ENTERPRISE_MCH_ID="1302099101"; //商户号
//	public static String IPHONE_ENTERPRISE_NOTIFY_URL="http://118.122.120.144:9876/DSORM/pay/yjg/wechat/app/iphoneEN/notify.do"; //支付异步通知地址
	
	public static String JS_APP_ID="wxd8bb6b975af45af5";
	public static String JS_APP_KEY="00112233445566778899AABBCCDDEEFF";  //应用密钥
	public static String JS_MCH_ID="1235233302"; //商户号
	public static String JS_NOTIFY_URL="http://dsorm.lejj.com/pay/yjg/wechat/js/notify.do"; //支付异步通知地址
	
	
	public static String NATIVE_APP_ID="wx1c305a5adc9565e1";
	public static String NATIVE_APP_KEY="00112233445566778899AABBCCDDEEFF";  //应用密钥
	public static String NATIVE_MCH_ID="1300155601"; //商户号
	public static String NATIVE_NOTIFY_URL="http://dsorm.lejj.com/pay/yjg/wechat/native/notify.do"; //支付异步通知地址
	private static Properties properties;
//	String JS_TYPE="JSAPI";
//	String TRADE_TYPE="APP"; //支付交易类型
//	String NOTIFY_URL="http://dsorm.lejj.com/pay/yjg/wechat/app/notify.do"; //支付异步通知地址
	static{
        properties = ConfigFileUtil.propertiesReader("wechatPayConfig.properties");
        APP_APP_ID=properties.getProperty("APP_APP_ID");
        APP_APP_KEY=properties.getProperty("APP_APP_KEY");
        APP_MCH_ID=properties.getProperty("APP_MCH_ID");
        APP_NOTIFY_URL=properties.getProperty("APP_NOTIFY_URL");
        
        MDB_APP_APP_ID=properties.getProperty("MDB_APP_APP_ID");
        MDB_APP_APP_KEY=properties.getProperty("MDB_APP_APP_KEY");
        MDB_APP_MCH_ID=properties.getProperty("MDB_APP_MCH_ID");
        MDB_APP_NOTIFY_URL=properties.getProperty("MDB_APP_NOTIFY_URL");
        
       
    	JS_APP_ID=properties.getProperty("JS_APP_ID");
    	JS_APP_KEY=properties.getProperty("JS_APP_KEY");
    	JS_MCH_ID=properties.getProperty("JS_MCH_ID");
    	JS_NOTIFY_URL=properties.getProperty("JS_NOTIFY_URL");
    	
    	NATIVE_APP_ID=properties.getProperty("NATIVE_APP_ID");
    	NATIVE_APP_KEY=properties.getProperty("NATIVE_APP_KEY");
    	NATIVE_MCH_ID=properties.getProperty("NATIVE_MCH_ID");
    	NATIVE_NOTIFY_URL=properties.getProperty("NATIVE_NOTIFY_URL");
    }
	
	public static String getWechatPayConfigValue(String key){
	    return properties.getProperty(key);
	}
}

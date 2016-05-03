package com.meiqi.openservice.action.pay.pc.upmp;

public class UpmpConfig {

    public static final String UPMP_GATEWAY_NEW ="https://gateway.95516.com/gateway/api/frontTransReq.do";
	public static final String merId="898320148160268";
	public static final String version = "5.0.0";
	public static final String encoding="UTF-8";
	public static final String SIGN_METHOD="01";
	public static final String txnType="01";
	public static final String txnSubType="01";
	public static final String bizType="000201";
	public static final String channelType="07";
	public static final String accessType="0";
	public static final String currencyCode="156";
	public static final String up="up";
	public static final String returnUrl_hmj="http://mall.lejj.com/paysucc/";//和美居同步通知地址
    public static final String notifyUrl_hmj="http://dsorm.lejj.com/pay/pc/upmp/backnotify/hmj.do";//和美居异步通知地址
}

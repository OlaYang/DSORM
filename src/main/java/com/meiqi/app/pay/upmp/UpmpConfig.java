package com.meiqi.app.pay.upmp;

public class UpmpConfig {

    public static boolean UPMP_TEST = false;

    // 版本号
    public static final String VERSION = "1.0.0";

    // 编码方式
    public static final String CHARSET = "UTF-8";

    // 交易网址
    public static final String TRADE_URL = "https://mgate.unionpay.com/gateway/merchant/trade";
    public static final String TRADE_URL_TEST = "http://202.101.25.178:8080/gateway/merchant/trade";

    // 查询网址
    public static final String QUERY_URL = "https://mgate.unionpay.com/gateway/merchant/query";
    public static final String QUERY_URL_TEST = "http://202.101.25.178:8080/gateway/merchant/query";

    // 商户代码
    public static final String MER_ID = "898320148160268";
    public static final String MER_ID_TEST = "880000000002707";

    // 通知URL
    public static final String MER_BACK_END_URL = "/pay/upmp/backnotify";

    // 前台通知URL
    public static final String MER_FRONT_END_URL = "";

    // 返回URL
    public static final String MER_FRONT_RETURN_URL = "";

    // 加密方式
    public static final String SIGN_TYPE = "MD5";

    // 商城密匙，需要和银联商户网站上配置的一样
    public static final String SECURITY_KEY = "MIIDtDCCAx2gAwIBAgIQbAT/PmXTZCJD/RxHs+DPyzANBgkqhkiG9w0BAQUFADAqMQswCQYDV"
            + "QQGEwJDTjEbMBkGA1UEChMSQ0ZDQSBPcGVyYXRpb24gQ0EyMB4XDTEwMDQxNTAyMTgzMloXDTE1MDUwNTA1MDYxOFowgYQxCzAJBgNVBA"
            + "YTAkNOMRswGQYDVQQKExJDRkNBIE9wZXJhdGlvbiBDQTIxETAPBgNVBAsTCExvY2FsIFJBMRQwEgYDVQQLEwtlbnRlcnByaXNlczEvMC0"
            + "GA1UEAxQmMDQxQDc3MzYyMzk4OS0wQDAwMDAwMDAxOlNJR05AMDAwMDAwMDEwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAKkh+CHT"
            + "LhUMPqUmTJtdSA/zhYgpRlqnJJZFOrlNfs/l3xRl5bEsSSwOLM55I0u97ykoGXGJQOxELp3oW9xxBjfOOTm1wz8dGrbnT1XFeZp1mtaAP"
            + "k5YNyt9AboeSVSzdZfsVKI+DEal7y9cph0vMB7i8CKyVhnbtyWLbeNEmp21AgMBAAGjggF+MIIBejAfBgNVHSMEGDAWgBTwje2zQbv77w"
            + "geVQLDMTfvPBROzTAdBgNVHQ4EFgQU4K4CiLrfrFQqIAOBbcJKZXlpPCMwCwYDVR0PBAQDAgXgMAwGA1UdEwQFMAMBAQAwHQYDVR0lBBY"
            + "wFAYIKwYBBQUHAwIGCCsGAQUFBwMEMIH9BgNVHR8EgfUwgfIwVqBUoFKkUDBOMQswCQYDVQQGEwJDTjEbMBkGA1UEChMSQ0ZDQSBPcGVy"
            + "YXRpb24gQ0EyMQwwCgYDVQQLEwNDUkwxFDASBgNVBAMTC2NybDEwNF8xNjczMIGXoIGUoIGRhoGObGRhcDovL2NlcnQ4NjMuY2ZjYS5jb"
            + "20uY246Mzg5L0NOPWNybDEwNF8xNjczLE9VPUNSTCxPPUNGQ0EgT3BlcmF0aW9uIENBMixDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbk"
            + "xpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDANBgkqhkiG9w0BAQUFAAOBgQCTAaiaH9+rlh2KrAPAQ3o4kXO"
            + "f5L67fQm2u/1qY2X3V32b3sTF1iCVNKGb9f0F/lRQfV7GAz/mCn0b/2eQLvDZU4Zl9E2wvlDN3EsmBcF3NJTQ09GEG6Dy7JiSUEcssATZ"
            + "Vg2iglvQSD828ujsIiyertzE9peAhx39TQTlk1nsiw==";
    public static final String SECURITY_KEY_TEST = "VHNaT1gIFeIoF2nrHAM22rPUl1R5Biio";

    /** *************************** */
    // 成功应答码
    public static final String RESPONSE_CODE_SUCCESS = "00";

    // 签名
    public static final String SIGNATURE = "signature";

    // 签名方法
    public static final String SIGN_METHOD = "signMethod";

    // 应答码
    public static final String RESPONSE_CODE = "respCode";

    // 应答信息
    public static final String RESPONSE_MSG = "respMsg";

    // 应答信息-交易流水号
    public static final String TRADE_NO = "tn";

    // 收到的金额
    public static final String RECEIVE_AMOUNT = "settleAmount";

    // 订单号
    public static final String ORDER_NUMBER = "orderNumber";

    // 交易状态
    public static final String TRADE_STATUS = "transStatus";

    // 交易成功结束
    public static final String TRADE_STATUS_SUCCESS = "00";

    // 订单分隔符
    String ORDERPAY = "up";

    // ///////////////////////////// app 内部银联相关的属性
    // //////////////////////////////
    // 支付渠道缓存时间
    public static final int PAY_CHANNEL_EXPIRE_TIME = 10 * 60;

}

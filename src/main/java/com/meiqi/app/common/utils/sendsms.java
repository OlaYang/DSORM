package com.meiqi.app.common.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class sendsms {
    private static final Logger LOG = Logger.getLogger(sendsms.class);
    private static final String URL = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";



    public static void main(String[] args) {

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(URL);

        // client.getParams().setContentCharset("GBK");
        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");

        int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);

        // System.out.println(mobile);

        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");
        // 提交短信数据
        // 密码可以使用明文密码或使用32位MD5加密
        NameValuePair[] data = { new NameValuePair("account", "cf_lejj2015"),
                new NameValuePair("password", "lejj123456"), new NameValuePair("mobile", "15608221361,15608221361"),
                new NameValuePair("content", content), };

        method.setRequestBody(data);

        try {
            client.executeMethod(method);

            String SubmitResult = method.getResponseBodyAsString();

            // System.out.println(SubmitResult);

//            Document doc = DocumentHelper.parseText(SubmitResult);
//            Element root = doc.getRootElement();
//
//            String code = root.elementText("code");
//            String msg = root.elementText("msg");
//            String smsid = root.elementText("smsid");
//
//            System.out.println(code);
//            System.out.println(msg);
//            System.out.println(smsid);
//
//            if ("2".equals(code)) {
//                System.out.println("短信提交成功");
//            }

        } catch (Exception e) {
            LOG.info(e);
        }

    }

}
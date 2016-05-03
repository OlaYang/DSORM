package com.meiqi.dsmanager.po.rule.smsSend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.util.LogUtil;

/**
 * @author 作者 xubao
 * @version 创建时间：2015年6月8日 下午2:03:34 类说明
 */

public class SMSSender {
	
    public SMSSender() {

    }



    /**
     * 短信发送方法
     * 
     * @param channelId
     *            发送的通道id
     * @param mobile
     *            发送的电话
     * @param content
     *            发送的内容
     * @return 发送的结果
     * @throws RengineException
     */
    public static String sendSms(D2Data data, SMSSend smsSend) {
        BaseRespInfo respInfo = new BaseRespInfo();
        String sendUrl = "";
        // 从map集合中取出通道对象
        try {
            // 检测是否存在于短信通道集合类中
            if (data.getValue("通道id", 0) != null) {
            	if (data.getValue("短信平台通道商名", 0).equals("上海助通")) {
                    // 拼接短信发送请求url
                    sendUrl = data.getValue("短信平台通道", 0) + "?username=" + data.getValue("短信平台用户名", 0) + "&password="
                            + data.getValue("短信平台密码", 0) + "&productid=" + data.getValue("短信平台产品id", 0) + "&mobile="
                            + smsSend.getPhoneNumber() + "&content=" + URLEncoder.encode(smsSend.getSendMsg(), "UTF-8");
                } else if (data.getValue("短信平台通道商名", 0).equals("上海拓鹏")) {
                    sendUrl = data.getValue("短信平台通道", 0) + "?userCode=" + data.getValue("短信平台用户名", 0) + "&userPass="
                            + data.getValue("短信平台密码", 0) + "&DesNo=" + smsSend.getPhoneNumber() + "&Msg="
                            + URLEncoder.encode(smsSend.getSendMsg() + data.getValue("拓鹏短信签名", 0), "UTF-8")
                            + "&Channel=" + data.getValue("短信平台产品id", 0);
                }
                // 短信发送,进行http请求
                String url = getUrl(sendUrl);
                respInfo.setCode("0");
                respInfo.setDescription(url);
            } else {
                respInfo.setCode("1");// 失败
                respInfo.setDescription("短信通道不存在！");
                LogUtil.error("短信通道不存在！");
            }
        } catch (Exception e) {
            respInfo.setCode("1");// 失败
            StringBuilder errMsg=new StringBuilder();
            try {
                errMsg.append("获取短信通道：");
                errMsg.append(data.getValue("短信平台通道", 0));
                errMsg.append("失败,请求路径：");
                errMsg.append(sendUrl);
                errMsg.append(" Error Detail：");
                errMsg.append(""+e);
                respInfo.setDescription(errMsg.toString());
                LogUtil.error(errMsg.toString());
            } catch (RengineException e1) {
                LogUtil.error("sendSms error："+e1.getMessage());
            }
            
        }
        return JSON.toJSONString(respInfo);
    }



    /**
     * @Title: getUrl
     * @Description: 获取地址
     * @param urlString
     * @return
     * @throws IOException
     */

    public static String getUrl(String urlString) throws IOException {
        StringBuffer sb = new StringBuffer();
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setReadTimeout(15000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        for (String line = null; (line = reader.readLine()) != null;) {
            sb.append(line + "\n");
        }
        reader.close();
        return URLDecoder.decode(sb.toString(), "UTF-8");
    }

}

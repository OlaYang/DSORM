package com.meiqi.app.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: SMSCode
 * @Description:短信发送验证码
 * @author 杨永川
 * @date 2015年7月6日 下午2:40:12
 *
 */
public class SMSCode {

    private String              phone;
    // 验证码类型
    // 0,注册短信
    // 1,找回密码短信
    // 2,更改手机绑定短信（验证旧手机号）
    // 3,银行卡号绑定短信
    // 4,更改手机绑定短信（验证新手机号）
    // 5,订单查询短信
    // 6,折扣码短信
    // 7,邀请码短信
    // 8,店铺信息
    // 9,手机验证登陆
    private byte                type;

    private String              msg;

    private Map<String, Object> param = new HashMap<String, Object>();



    public SMSCode() {
        super();
    }



    public SMSCode(String phone, byte type) {
        super();
        this.phone = phone;
        this.type = type;
    }



    public String getPhone() {
        return phone;
    }



    public void setPhone(String phone) {
        this.phone = phone;
    }



    public byte getType() {
        return type;
    }



    public void setType(byte type) {
        this.type = type;
    }



    public String getMsg() {
        return msg;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }



    public Map<String, Object> getParam() {
        return param;
    }



    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

}

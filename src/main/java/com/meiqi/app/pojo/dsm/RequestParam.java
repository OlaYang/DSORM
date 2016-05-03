package com.meiqi.app.pojo.dsm;

import java.util.Map;

/**
 * 
 * @ClassName: RequsetParam
 * @Description:调用DSM 请求参数子类
 * @author 杨永川
 * @date 2015年6月24日 下午2:12:55
 *
 */
public class RequestParam {
    // 需要发送短信的目标手机号
    private String              phoneNumber;
    // 有效请求校验码
    private String              auth_key;
    // 短信模板id
    private String              templateId;
    // 模板里面使用的参数
    private Map<String, Object> param;



    public RequestParam() {
        super();
    }



    public RequestParam(String phoneNumber, String auth_key, String templateId, Map<String, Object> param) {
        super();
        this.phoneNumber = phoneNumber;
        this.auth_key = auth_key;
        this.templateId = templateId;
        this.param = param;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }



    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getAuth_key() {
        return auth_key;
    }



    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }



    public String getTemplateId() {
        return templateId;
    }



    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }



    public Map<String, Object> getParam() {
        return param;
    }



    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

}

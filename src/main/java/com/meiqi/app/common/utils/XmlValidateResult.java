package com.meiqi.app.common.utils;

/**
 * 
 * @ClassName: XmlValidateResult
 * @Description:Xml验证结果
 * @author 杨永川
 * @date 2015年5月19日 上午10:54:19
 *
 */
public class XmlValidateResult {
    // 是否通过验证
    private boolean validated;

    // 错误信息
    private String  errorMsg;



    /**
     * 构造函数，默认为不通过，错误原因为空字符串
     */
    public XmlValidateResult() {
        validated = false;
        errorMsg = "";
    }



    public XmlValidateResult(boolean validated, String errorMsg) {
        this.validated = validated;
        this.errorMsg = errorMsg;
    }



    public String getErrorMsg() {
        return errorMsg;
    }



    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }



    public boolean isValidated() {
        return validated;
    }



    public void setValidated(boolean validated) {
        this.validated = validated;
    }

}
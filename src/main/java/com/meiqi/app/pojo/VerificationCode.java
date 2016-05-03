package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: VerificationCode
 * @Description:验证码
 * @author 杨永川
 * @date 2015年4月22日 下午9:11:50
 *
 */
public class VerificationCode {
    private long   codeId;
    // 验证码类型 0=注册 1=找回密码 2=更改手机绑定(旧手机) 3=银行卡4=修改绑定手机(新手机)
    private byte   type;
    private String objectId;
    private String codeValue;
    private int    validTime;
    private byte   isValid = 0;
    // RequestBody 临时属性
    private String phone;
    private String code;

    private boolean makeInvalid =true; //短信验证码是否验证后立即失效 true失效 false不失效


    public VerificationCode() {
        super();
    }



    public VerificationCode(long codeId, byte type, String objectId, String codeValue, int validTime, byte isValid) {
        super();
        this.codeId = codeId;
        this.type = type;
        this.objectId = objectId;
        this.codeValue = codeValue;
        this.validTime = validTime;
        this.isValid = isValid;
    }



    public boolean isMakeInvalid() {
		return makeInvalid;
	}



	public void setMakeInvalid(boolean makeInvalid) {
		this.makeInvalid = makeInvalid;
	}



	public long getCodeId() {
        return codeId;
    }



    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }



    public byte getType() {
        return type;
    }



    public void setType(byte type) {
        this.type = type;
    }



    public String getObjectId() {
        return objectId;
    }



    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }



    public String getCodeValue() {
        return codeValue;
    }



    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }



    public int getValidTime() {
        return validTime;
    }



    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }



    public byte getIsValid() {
        return isValid;
    }



    public void setIsValid(byte isValid) {
        this.isValid = isValid;
    }



    public String getPhone() {
        return phone;
    }



    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }

}

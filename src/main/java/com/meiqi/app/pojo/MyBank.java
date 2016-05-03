package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: MyBank
 * @Description:设计师绑定的银行卡
 * @author 杨永川
 * @date 2015年5月27日 下午4:10:54
 *
 */
public class MyBank {
    @ColumnKey(value = "myBankId")
    private long   myBankId     = 0;
    @ColumnKey(value = "userId")
    private long   userId       = 0;
    private long   bankId       = 0;
    @ColumnKey(value = "userRealName")
    private String userRealName = "";
    @ColumnKey(value = "cardNumber")
    private String cardNumber   = "";
    private int    addTime      = 0;

    // 临时属性
    @ColumnKey(value = "bankId")
    private Bank   bank;
    // 验证码
    private String code;



    public MyBank() {
        super();
    }



    public MyBank(long myBankId, long userId, long bankId, String userRealName, String cardNumber, int addTime) {
        super();
        this.myBankId = myBankId;
        this.userId = userId;
        this.bankId = bankId;
        this.userRealName = userRealName;
        this.cardNumber = cardNumber;
        this.addTime = addTime;
    }



    public long getMyBankId() {
        return myBankId;
    }



    public void setMyBankId(long myBankId) {
        this.myBankId = myBankId;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public long getBankId() {
        return bankId;
    }



    public void setBankId(long bankId) {
        this.bankId = bankId;
    }



    public String getUserRealName() {
        return userRealName;
    }



    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }



    public String getCardNumber() {
        return cardNumber;
    }



    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }



    public Bank getBank() {
        return bank;
    }



    public void setBank(Bank bank) {
        this.bank = bank;
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }

}

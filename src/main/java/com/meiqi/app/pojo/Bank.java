package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: Bank
 * @Description:银行
 * @author 杨永川
 * @date 2015年5月27日 下午3:55:50
 *
 */
public class Bank {
    @ColumnKey(value = "bankId")
    private long   bankId     = 0;
    @ColumnKey(value = "bankName")
    private String bankName   = "";
    @ColumnKey(value = "bankLogo")
    private String bankLogo   = "";
    @ColumnKey(value = "bankRemark")
    private String bankRemark = "";
    private int    sortOrder  = 0;



    public Bank() {
        super();
    }



    public Bank(long bankId, String bankName, String bankLogo, String bankRemark, int sortOrder) {
        super();
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankLogo = bankLogo;
        this.bankRemark = bankRemark;
        this.sortOrder = sortOrder;
    }



    public long getBankId() {
        return bankId;
    }



    public void setBankId(long bankId) {
        this.bankId = bankId;
    }



    public String getBankName() {
        return bankName;
    }



    public void setBankName(String bankName) {
        this.bankName = bankName;
    }



    public String getBankLogo() {
        return bankLogo;
    }



    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
    }



    public String getBankRemark() {
        return bankRemark;
    }



    public void setBankRemark(String bankRemark) {
        this.bankRemark = bankRemark;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

}

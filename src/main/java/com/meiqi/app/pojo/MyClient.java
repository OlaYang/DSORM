package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: MyClient
 * @Description:设计师与客户地址关系表
 * @author 杨永川
 * @date 2015年4月24日 下午6:49:39
 *
 */
public class MyClient {
    private long        myClientId;
    private UserAddress consignee;
    private long        designerId;
    private int         addTime;



    public MyClient() {
    }



    public MyClient(long myClientId, UserAddress consignee, long designerId, int addTime) {
        super();
        this.myClientId = myClientId;
        this.consignee = consignee;
        this.designerId = designerId;
        this.addTime = addTime;
    }



    public long getMyClientId() {
        return myClientId;
    }



    public void setMyClientId(long myClientId) {
        this.myClientId = myClientId;
    }



    public UserAddress getConsignee() {
        return consignee;
    }



    public void setConsignee(UserAddress consignee) {
        this.consignee = consignee;
    }



    public long getDesignerId() {
        return designerId;
    }



    public void setDesignerId(long designerId) {
        this.designerId = designerId;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }

}
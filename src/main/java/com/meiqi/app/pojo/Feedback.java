package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Feedback
 * @Description:用户反馈信息表，包括留言，投诉，咨询等
 * @author 杨永川
 * @date 2015年5月26日 下午6:00:11
 *
 */
public class Feedback {
    private long   msgId;
    private long   parentId   = 0;
    private long   userId     = 0;
    private String userName   = "";
    private String userEmail  = "";
    private String msgTitle   = "";
    private int    msgType    = 0;
    private long   msgStatus  = 0;
    private String msgContent = "";
    private int    msgTime    = 0;
    private String messageImg = "";
    private long   orderId    = 0;
    private int    msgArea    = 0;
    // 0:pc， 1:安卓， 2:ios
    private int    plat       = 0;



    public Feedback() {
    }



    public Feedback(long msgId, long parentId, long userId, String userName, String userEmail, String msgTitle,
            int msgType, long msgStatus, String msgContent, int msgTime, String messageImg, long orderId, int msgArea,
            int plat) {
        super();
        this.msgId = msgId;
        this.parentId = parentId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.msgTitle = msgTitle;
        this.msgType = msgType;
        this.msgStatus = msgStatus;
        this.msgContent = msgContent;
        this.msgTime = msgTime;
        this.messageImg = messageImg;
        this.orderId = orderId;
        this.msgArea = msgArea;
        this.plat = plat;
    }



    public long getMsgId() {
        return msgId;
    }



    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }



    public long getParentId() {
        return parentId;
    }



    public void setParentId(long parentId) {
        this.parentId = parentId;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String getUserName() {
        return userName;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getUserEmail() {
        return userEmail;
    }



    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }



    public String getMsgTitle() {
        return msgTitle;
    }



    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }



    public int getMsgType() {
        return msgType;
    }



    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }



    public long getMsgStatus() {
        return msgStatus;
    }



    public void setMsgStatus(long msgStatus) {
        this.msgStatus = msgStatus;
    }



    public String getMsgContent() {
        return msgContent;
    }



    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }



    public int getMsgTime() {
        return msgTime;
    }



    public void setMsgTime(int msgTime) {
        this.msgTime = msgTime;
    }



    public String getMessageImg() {
        return messageImg;
    }



    public void setMessageImg(String messageImg) {
        this.messageImg = messageImg;
    }



    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public int getMsgArea() {
        return msgArea;
    }



    public void setMsgArea(int msgArea) {
        this.msgArea = msgArea;
    }



    public int getPlat() {
        return plat;
    }



    public void setPlat(int plat) {
        this.plat = plat;
    }

}
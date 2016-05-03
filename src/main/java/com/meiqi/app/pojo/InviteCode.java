package com.meiqi.app.pojo;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;

/**
 * 
 * @ClassName: InviteCode
 * @Description:设计师邀约码
 * @author 杨永川
 * @date 2015年6月18日 下午4:01:33
 *
 */
public class InviteCode {
    private long   inviteId;
    private String code;
    private String receivePhone;
    private long   sendUserId;
    private long   useUserId;
    // 邀约码状态 0=未使用,1=使用
    private byte   status    = 0;
    private int    sendTime;
    private int    useTime;

    // 临时属性
    private String sendDate;
    private int    pageIndex = 0;
    private int    pageSize  = 0;



    public InviteCode() {
        super();
    }



    public InviteCode(long inviteId, String code, String receivePhone, long sendUserId, long useUserId, byte status,
            int sendTime, int useTime) {
        super();
        this.inviteId = inviteId;
        this.code = code;
        this.receivePhone = receivePhone;
        this.sendUserId = sendUserId;
        this.useUserId = useUserId;
        this.status = status;
        this.sendTime = sendTime;
        this.useTime = useTime;
    }



    public long getInviteId() {
        return inviteId;
    }



    public void setInviteId(long inviteId) {
        this.inviteId = inviteId;
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }



    public String getReceivePhone() {
        return receivePhone;
    }



    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }



    public long getSendUserId() {
        return sendUserId;
    }



    public void setSendUserId(long sendUserId) {
        this.sendUserId = sendUserId;
    }



    public long getUseUserId() {
        return useUserId;
    }



    public void setUseUserId(long useUserId) {
        this.useUserId = useUserId;
    }



    public byte getStatus() {
        return status;
    }



    public void setStatus(byte status) {
        this.status = status;
    }



    public int getSendTime() {
        return sendTime;
    }



    public void setSendTime(int sendTime) {
        this.sendTime = sendTime;
        if (0 != sendTime) {
            setSendDate(DateUtils.timeToDate((long) sendTime * 1000, ContentUtils.TIME_FORMAT2));
        }
    }



    public int getUseTime() {
        return useTime;
    }



    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }



    public String getSendDate() {
        return sendDate;
    }



    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }



    public int getPageIndex() {
        return pageIndex;
    }



    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }



    public int getPageSize() {
        return pageSize;
    }



    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}

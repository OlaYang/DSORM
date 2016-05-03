package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: ApplyEntryLog
 * @Description:客户申请入驻记录
 * @author 杨永川
 * @date 2015年6月17日 下午7:37:25
 *
 */
public class ApplyEntryLog {
    private long   logId;
    private long   userId;
    // 申请入驻状态 0=待审核 1=通过 2=驳回3=作废 4=未申请
    private byte   applyStatus = 4;
    // 申请记录 如未通过原因..
    private String applyLog    = "";
    private int    applyTime   = 0;
    private String remark      = "";

    // 入驻状态url;
    private String url;



    public ApplyEntryLog() {
        super();
    }



    public ApplyEntryLog(long logId, long userId, byte applyStatus, String applyLog, int applyTime, String remark) {
        super();
        this.logId = logId;
        this.userId = userId;
        this.applyStatus = applyStatus;
        this.applyLog = applyLog;
        this.applyTime = applyTime;
        this.remark = remark;
    }



    public long getLogId() {
        return logId;
    }



    public void setLogId(long logId) {
        this.logId = logId;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public byte getApplyStatus() {
        return applyStatus;
    }



    public void setApplyStatus(byte applyStatus) {
        this.applyStatus = applyStatus;
    }



    public String getApplyLog() {
        return applyLog;
    }



    public void setApplyLog(String applyLog) {
        this.applyLog = applyLog;
    }



    public int getApplyTime() {
        return applyTime;
    }



    public void setApplyTime(int applyTime) {
        this.applyTime = applyTime;
    }



    public String getRemark() {
        return remark;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }



    public String getUrl() {
        return url;
    }



    public void setUrl(String url) {
        this.url = url;
    }

}

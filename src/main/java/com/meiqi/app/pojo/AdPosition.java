package com.meiqi.app.pojo;

/**
 * 广告位(名称,宽,高,描述,样式)
 * 
 * @ClassName: EcsAdPosition
 * @Description:
 * @author 杨永川
 * @date 2015年4月7日 下午3:52:44
 *
 */
public class AdPosition {
    private int    positionId;
    // 广告位中文名称
    private String adName;
    // 广告代码
    private String adCode;
    // 是否有效
    private int    enabled = 1;
    // 1安卓，2ios，3ipad，4乐家居，5和美居,分别逗号分隔
    private String appType;
    // 分组
    private long   gid;
    // 权限代码
    private long   pid;
    private int    aTime;



    public AdPosition() {
    }



    public AdPosition(int positionId, String adName, String adCode, int enabled, String appType, long gid, long pid,
            int aTime) {
        super();
        this.positionId = positionId;
        this.adName = adName;
        this.adCode = adCode;
        this.enabled = enabled;
        this.appType = appType;
        this.gid = gid;
        this.pid = pid;
        this.aTime = aTime;
    }



    public int getPositionId() {
        return positionId;
    }



    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }



    public String getAdName() {
        return adName;
    }



    public void setAdName(String adName) {
        this.adName = adName;
    }



    public String getAdCode() {
        return adCode;
    }



    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }



    public int getEnabled() {
        return enabled;
    }



    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }



    public String getAppType() {
        return appType;
    }



    public void setAppType(String appType) {
        this.appType = appType;
    }



    public long getGid() {
        return gid;
    }



    public void setGid(long gid) {
        this.gid = gid;
    }



    public long getPid() {
        return pid;
    }



    public void setPid(long pid) {
        this.pid = pid;
    }



    public int getaTime() {
        return aTime;
    }



    public void setaTime(int aTime) {
        this.aTime = aTime;
    }

}
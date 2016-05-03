package com.meiqi.data.po;

/**
 * User: 
 * Date: 14-2-13
 * Time: 上午10:54
 */
public class TServiceAlarmPo {
    private Integer logid;
    private String sname;
    private String msg;


    public TServiceAlarmPo(String sname, String msg) {
        this.sname = sname;
        this.msg = msg;
    }

    public Integer getLogid() {
        return logid;
    }

    public void setLogid(Integer logid) {
        this.logid = logid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    @Override
    public String toString() {
        return sname == null ? msg : sname + ": " + msg;
    }
}

package com.meiqi.data.entity;

import java.io.Serializable;

/**
 * User: Date: 14-2-13 Time: 上午10:54
 */
public class TServiceAlarm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer logid;
	private String sname;
	private String msg;

	public TServiceAlarm(String sname, String msg) {
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

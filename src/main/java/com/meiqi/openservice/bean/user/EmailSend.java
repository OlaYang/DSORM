/*
* File name: EmailSend.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			luzicong		2015年11月18日
* ...			...			...
*
***************************************************/

package com.meiqi.openservice.bean.user;

/**
 * <class description>
 *		
 * @author: luzicong
 * @version: 1.0, 2015年11月18日
 */

public class EmailSend {
    long id;
    long userId;
    String email;
    String url;
    int type;
    int sendTime;
    boolean valid = false;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getSendTime() {
        return sendTime;
    }
    public void setSendTime(int sendTime) {
        this.sendTime = sendTime;
    }
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    
}

/*
 * File name: RenderingCollect.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年9月10日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.bean;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年9月10日
 */

public class RenderingCollect {
    int userId;
    int renderingId;
    boolean collected = false;
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getRenderingId() {
        return renderingId;
    }
    public void setRenderingId(int renderingId) {
        this.renderingId = renderingId;
    }
    public boolean isCollected() {
        return collected;
    }
    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}

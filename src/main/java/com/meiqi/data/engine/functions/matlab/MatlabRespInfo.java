package com.meiqi.data.engine.functions.matlab;

import com.meiqi.data.handler.BaseRespInfo;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-21
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
public class MatlabRespInfo extends BaseRespInfo {
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

/*
 * File name: UploadFileService.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年7月24日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.service;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.openservice.commons.exception.OpenServiceException;

public interface IUploadFileService {

    public abstract String upload(HttpServletRequest request, String path, String fileName) throws Exception;

    public abstract String uploadToFileServer(HttpServletRequest request) throws OpenServiceException;
    
    /**
     * 上传图片到图片服务器，并返回绝对地址和相对地址
     * @param filePath  本地图片地址
     * @return  jsonobject key=file 图片绝对地址  path 图片相对地址
     * @throws Exception
     */
    public abstract JSONObject uploadToFileServer(String filePath);

}
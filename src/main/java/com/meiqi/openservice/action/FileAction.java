/*
 * File name: FileAction.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年7月29日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.service.IUploadFileService;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年7月29日
 */
@Service
public class FileAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(FileAction.class);

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;

    @Autowired
    private IUploadFileService  uploadFileService;



    public String uploadToFileServer(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.debug("Function: uploadToFileServer.Start.");
        ResponseInfo respInfo = new ResponseInfo();

        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 上传文件
        String filePath = null;
        try {
            filePath = uploadFileService.uploadToFileServer(request);
        } catch (Exception e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        // 返回成功
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        respInfo.setObject(filePath);

        LOG.debug("Function: uploadToFileServer.End.");
        return JSON.toJSONString(respInfo);
    }
}

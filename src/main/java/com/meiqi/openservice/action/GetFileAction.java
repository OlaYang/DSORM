package com.meiqi.openservice.action;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.bean.RepInfo;

/**
 * 根据传入的数据库信息或者url，获取对应文件内容，并返回给客户端
 * @author wanghuanwei
 * @date 2015/07/21
 * @version 1.0
 * */
@Service
public class GetFileAction extends BaseAction{

    @Autowired
    private IDataAction dataAction;
    
    public void getFile(HttpServletRequest req, HttpServletResponse resp,RepInfo repInfo)
    {
        // 先通过请求内容，获取图片的src
        String content = repInfo.getParam();
        try {
            content = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DsManageReqInfo dsReqInfo = DataUtil.parse(content, DsManageReqInfo.class);
        String resultData = dataAction.getData(dsReqInfo,content);
       
        String fileUrlStr = "";
        
        try {
            resp.setContentType("application/octet-stream");
            
            URL fileUrl = new URL(fileUrlStr);
            HttpURLConnection httpUrlConn = (HttpURLConnection) fileUrl.openConnection();
            httpUrlConn.connect();
            
            BufferedInputStream fileStream = new BufferedInputStream(httpUrlConn.getInputStream());
            
            OutputStream outputStream = resp.getOutputStream();
            byte[] b = new byte[1024];
            int hasRead = 0;
            while ((hasRead = fileStream.read(b)) != -1) {
                outputStream.write(b, 0, hasRead);
            }
            outputStream.flush();
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}

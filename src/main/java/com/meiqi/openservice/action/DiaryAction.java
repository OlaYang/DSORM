/**
 * @Title: RegisterAction.java
 * @Package com.meiqi.openservice.action.register
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * @version V1.0
 */
package com.meiqi.openservice.action;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.pay.alipay.sign.Base64;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.Tool;

/**
 * @ClassName: RegisterAction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * 
 */
@Service
public class DiaryAction extends BaseAction {

    @Autowired
    private IMushroomAction mushroomAction;

    
    public Object addDiary(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        ResponseInfo respInfo=new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        String content = repInfo.getParam();
        JSONObject object=JSONObject.parseObject(content);
        String normalVerifyCode=object.getString("normalVerifyCode");
        if("".equals(normalVerifyCode)){
            respInfo.setCode(DsResponseCodeData.REGISTER_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.REGISTER_CODE_NOT_RIGHT.description);
            return respInfo;
        }
        DsManageReqInfo dsReqInfo = DataUtil.parse(content, DsManageReqInfo.class);
        //验证输入的装修案例验证码的正确性
        boolean r=Tool.verifyCode(request, normalVerifyCode,com.meiqi.openservice.commons.config.Constants.CodeType.ADD_DIARY,true);
        if(!r){
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            return respInfo;
        }
        return mushroomAction.offer(dsReqInfo,request,response);
    }

}

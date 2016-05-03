package com.meiqi.openservice.action;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.MemcacheLejjClient;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.ValidateXmlUtil;
import com.meiqi.openservice.service.IApplyService;
import com.meiqi.openservice.vo.ApplyVo;
import com.meiqi.util.LogUtil;
import com.schooner.MemCached.MemcachedItem;

@Component
public class ApplyAction extends BaseAction
{
    private final static String MOBILE_KEY = "zx_mobile_";
    private final static String ECS_ID = "ECS_ID";
    private final static String SESSION_KEY = "session_table_";
    private final static String USER_ID = "user_id";
    
    @Autowired
    private IApplyService applyService;
    
    @Autowired
    private MemcacheLejjClient memcacheLejjClient;
    
    public String saveApply(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
    	String param = repInfo.getParam();
        ApplyVo apply = DataUtil.parse(param, ApplyVo.class);
//        boolean isCorrect = Tool.verifyCode(request, apply.getVerifyCode(), apply.getCodeType());
//        if(!isCorrect){
//            return JSON.toJSONString(new ResponseInfo(DsResponseCodeData.ERROR.code, "验证码错误"));
//        }
        return doSave(request, apply);
    }

    private String doSave(HttpServletRequest request, ApplyVo apply)
    {
        String xmlPath = getClass().getResource(ValidateXmlUtil.XML_PATH + apply.getFormName() + ValidateXmlUtil.XML_SUFFIX).getPath();
        String xsdPath = getClass().getResource(ValidateXmlUtil.XML_PATH + apply.getFormName() + ValidateXmlUtil.XSD_SUFFIX).getPath();
        try {
            long userId = 0;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (StringUtils.equals(cookie.getName(), ECS_ID)) {
                        if(cookie.getValue() != null){
                            userId = getUserId(cookie);
                        }
                        memcacheLejjClient.put(MOBILE_KEY + cookie.getValue().substring(0, 32), apply.getPhone());
                        break;
                    }
                }
            }
            apply.setUserId(userId);
            ResponseInfo resp = applyService.saveRegisterinfo(xmlPath, xsdPath, apply);
            return JSON.toJSONString(resp);
        } catch (Exception e) {
            LogUtil.error("applyAction save error:");
            LogUtil.error(e);
            return JSON.toJSONString(new ResponseInfo(DsResponseCodeData.ERROR.code, "传入数据有误"));
        }
    }
    
    @SuppressWarnings("rawtypes")
    private long getUserId(Cookie cookie) throws IOException, JsonParseException, JsonMappingException {
        MemcachedItem item = memcacheLejjClient.gets(SESSION_KEY + cookie.getValue().substring(0, 32));
        if(item == null){
            return 0L;
        }
        Object value = item.getValue();
        if(value == null){
            return 0L;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map readValue = mapper.readValue(value.toString(), Map.class);
        Map zxMap = (Map)readValue.get("zx");
        if(zxMap == null){
            return 0L;
        }
        Object userIdObj = zxMap.get(USER_ID);
        if(userIdObj != null){
            return Long.parseLong(userIdObj.toString());
        }
        return 0L;
    }
    
    public String saveApplySimple(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
    	String param = repInfo.getParam();
        ApplyVo apply = DataUtil.parse(param, ApplyVo.class);
        return doSave(request, apply);
    }
}

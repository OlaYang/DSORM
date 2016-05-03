package com.meiqi.dsmanager.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.dsmanager.action.IAuthAction;
import com.meiqi.dsmanager.action.ICacheAction;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.AuthBean;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.rmi.impl.RmiSolrService;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.commons.util.Base64;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.VerifySqlAttackUtil;
import com.meiqi.thread.ThreadHelper;

@RequestMapping("/service")
@Controller
public class ServiceController {
	private static final Logger LOG = Logger.getLogger(RmiSolrService.class);
	
	@Autowired
    private IDataAction dataAction;
	
	@Autowired
	private IMushroomAction mushroomAction;
	
	@Autowired
	private ThreadHelper  indexTheadHelper;
	
	@Autowired
	private ISolrAction solrAction;
	
	@Autowired
    private ICacheAction cacheAction;
	
	@Autowired
	private IMemcacheAction memcacheService;
	
	 @Autowired
	 private IAuthAction authAction;
	
	private static final String FILE_SEPARATOR = "/";

	@RequestMapping(value = "/getData")
	@ResponseBody
	public String getData(HttpServletRequest request,HttpServletResponse response) throws IOException {
	    long begin=System.currentTimeMillis();
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		//LogUtil.info(Thread.currentThread().getName() + Thread.currentThread().getId() + "recive get request" + System.currentTimeMillis());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		
		content = content.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            LogUtil.error(e.getMessage());
        }
		
		if(VerifySqlAttackUtil.verify(decodeContent)){
	            ResponseBaseData re=new ResponseBaseData();
	            re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
	            re.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
	            return JSONObject.toJSONString(re);
	    }
		  
		if(StringUtils.isEmpty(content)){
		    ResponseBaseData re=new ResponseBaseData();
		    re.setCode(DsResponseCodeData.REQINFO_NOT_RIGHT.code);
		    re.setDescription(DsResponseCodeData.REQINFO_NOT_RIGHT.description);
		    return JSONObject.toJSONString(re);
		}
		LogUtil.info("ServiceController"+",reqInfo:"+content);
        long startTime = System.currentTimeMillis();
        DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
        
        // 请求授权验证
        AuthBean authBean = authAction.validateAuthForPc(request, Base64.encode(decodeContent.getBytes()));
        if (!authBean.isState()) {
            return JsonUtils.getFrequentRequestErrorJson();
        }
        
        long spentTime = 0L;
        startTime = System.currentTimeMillis();
        String resultData = dataAction.getData(dsReqInfo,decodeContent);
        spentTime = System.currentTimeMillis() - startTime;
        LogUtil.info(Thread.currentThread().getName() + " get data total time" + spentTime);
        
        // 如果是登录操作则向memcached中写入缓存
        if ("LJG_HSV1_orderpaylogin".equals(dsReqInfo.getServiceName())) {
            doLogin(dsReqInfo.getServiceName(), resultData, dsReqInfo, request, response);
        }
        if (BaseAction.isFromApp(request)) {
            BaseAction.putJsonToResponse(response, resultData);
            return null;
        }
        long end=System.currentTimeMillis();
        long time=end-begin;
        LogUtil.info("ServiceController execute time:"+time+",reqInfo:"+content);
        return resultData;

	}
	
	public void doLogin(String serviceName,String resultData,DsManageReqInfo dsReqInfo,HttpServletRequest request,HttpServletResponse response){

        RuleServiceResponseData resp=DataUtil.parse(resultData, RuleServiceResponseData.class);
        if(DsResponseCodeData.SUCCESS.code.equals(resp.getCode())){
            Map<String, String> result=resp.getRows().get(0);
            if("1".equals(result.get("login"))){
                String userId=result.get("userID");
                Map<String,Object> param=dsReqInfo.getParam();
                String isAutoLogin=param.get("isAutoLogin").toString();
                String type=param.get("type").toString();
                String key="islogin_"+userId+"_"+type;
                long time=0;
                if("0".equals(isAutoLogin)){
                    //是非自动登录，缓存时间设置为30分钟
                    time=30 * 60 * 1000;
                }else{
                    //是自动登录，缓存时间设置为半年
                    time=6*24*60 * 60 * 1000;
                }
                String isAutoLoginKey="isAutologin_"+userId+"_"+type;
                memcacheService.putCache(isAutoLoginKey,isAutoLogin);
                memcacheService.putCache(key, JSONObject.toJSONString(result),time);
                Cookie cookie=new Cookie("uid",userId);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
         }	  
	}
	
	@RequestMapping(value = "/setData")
	@ResponseBody
	public String setData(HttpServletRequest request,HttpServletResponse response) throws IOException  {
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		if(VerifySqlAttackUtil.verify(decodeContent)){
            ResponseBaseData re=new ResponseBaseData();
            re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            re.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(re);
        }
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		
		// 请求授权验证
        AuthBean validateAuth = authAction.validateAuthForPc(request, Base64.encode(decodeContent.getBytes()));
        if (!validateAuth.isState()) {
            return JsonUtils.getFrequentRequestErrorJson();
        }
		
		String resultData = mushroomAction.offer(dsReqInfo,request,response);
		if (BaseAction.isFromApp(request)) {
		    BaseAction.putJsonToResponse(response, resultData);
            return null;
        }
		//根据前端传过来的key 清除memcache缓存
		Cookie[] cookies=request.getCookies();
	        for(Cookie cookie:cookies){
	              String cookieName=cookie.getName();
	              if("removeMemCacheKey".equals(cookieName)){
	                  String key=cookie.getValue();
	                  boolean removeCacheResult=memcacheService.removeCache(key);
	                  //LogUtil.info("set removeMemCacheKey:"+key+",result:"+removeCacheResult);
	                  break;
	              }
	    }
		return resultData;
	}
	
	@RequestMapping("/start")
	@ResponseBody
	public String start(HttpServletRequest request) throws IOException {
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		return mushroomAction.start(dsReqInfo);
	}

	@RequestMapping("/commit")
	@ResponseBody
	public String commit(HttpServletRequest request) throws IOException {
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		return mushroomAction.commit(dsReqInfo);
	}

	@RequestMapping("/rollback")
	@ResponseBody
	public String rollback(HttpServletRequest request) throws IOException {
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);;
		return mushroomAction.rollback(dsReqInfo);
	}
	
	@RequestMapping("/querySolr")
	@ResponseBody
	public String query(HttpServletRequest request,HttpServletResponse response) throws IOException {
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
			decodeContent=DataUtil.clearEq(decodeContent);
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		DsManageReqInfo reqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		String resultData = solrAction.query(reqInfo);
        if (BaseAction.isFromApp(request)) {
            BaseAction.putJsonToResponse(response, resultData);
            return null;
        }
		return resultData;
	}
	
}

/**   
* @Title: RegisterAction.java 
* @Package com.meiqi.openservice.action.register 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月8日 上午11:02:08 
* @version V1.0   
*/
package com.meiqi.openservice.action.login;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @ClassName: LoginVerifyAction 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08 
 *  
 */
@Service
public class LoginVerifyAction extends BaseAction{

    @Autowired
    private IMemcacheAction memcacheService;
    
	public Object verifyIsLogin(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
	    ResponseInfo respInfo=new ResponseInfo();
	    Map<String,String> map=null;
	    try {
    	    map=DataUtil.parse(repInfo.getParam(),Map.class);
    	    String userId=map.get("userId")==null?"":map.get("userId").toString();
    	    String type=map.get("type")==null?"":map.get("type").toString();
    	    if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(type)){
    	        respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(DsResponseCodeData.ERROR.description);
                return respInfo;
    	    }
    	    HttpSession session=request.getSession();
            String key="islogin_"+userId+"_"+type;
            String sessionIdLoginKey="login_"+request.getSession().getId();
            Object object=session.getAttribute(key);
            if(object!=null){
                //有可能session没有过期，但是缓存已经过期,那么缓存数据就要重新写入一次
                if(memcacheService.getCache(key)==null || memcacheService.getCache(sessionIdLoginKey)==null){
                    Map<String, Object> result = DataUtil.parse(object.toString());
                    String isAutoLoginKey="isAutologin_"+userId+"_"+type;
                    putCookieAndMemcache(request, response, result, userId, key, isAutoLoginKey);
                }
                respInfo.setCode(DsResponseCodeData.SUCCESS.code);
                respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
                respInfo.setObject(object);
                return respInfo;
            }else if((object=memcacheService.getCache(key))!=null){
                //有可能是自动登录，那么缓存三天才过期，session已经过期了，那么session的值需要重新写入
                respInfo.setCode(DsResponseCodeData.SUCCESS.code);
                respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
                respInfo.setObject(object);
                session.setAttribute(key, object.toString());
                return respInfo;
            }else{
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("用户已经处于未登录状态，session已过期而且缓存已失效");
                return respInfo;
            }
	  } catch (Exception e) {
	      LogUtil.error("登录异常,param:"+map+",error:"+e);
	      respInfo.setCode(DsResponseCodeData.ERROR.code);
          respInfo.setDescription(DsResponseCodeData.ERROR.description);
          return respInfo;
      }
	}
	
	
	public void putCookieAndMemcache(HttpServletRequest request,HttpServletResponse response,Map<String, Object> result, String userId,String key,String isAutoLoginKey){
	    String jsonResult=JSONObject.toJSONString(result);
        long time = 0;
        Cookie uid = new Cookie("uid", userId);
        Cookie name;
        try {
            name = new Cookie("userName",URLEncoder.encode(result.containsKey("userName")?result.get("userName").toString():"","UTF-8"));
            uid.setPath("/");
            name.setPath("/");
            Object isAutologin=memcacheService.getCache(isAutoLoginKey);//此key不过期
            if (isAutologin!=null && "0".equals(isAutologin.toString())) {
                // 是非自动登录，缓存时间设置为60分钟
                time = 60 * 60 * 1000;
                uid.setMaxAge(3600);//1小时
                name.setMaxAge(3600);//1小时
            } else {
                // 是自动登录，缓存时间设置为3天
                time = 3 * 24 * 60 * 60 * 1000;
                uid.setMaxAge(3 * 24 * 60 * 60);
                name.setMaxAge(3 * 24 * 60 * 60);
            }
            response.addCookie(uid);
            response.addCookie(name);
            memcacheService.putCache(key, jsonResult , time);
        } catch (Exception e) {
            LogUtil.error("LoginVerifyAction putCookieAndMemcache error:"+e);
        }
	}
}

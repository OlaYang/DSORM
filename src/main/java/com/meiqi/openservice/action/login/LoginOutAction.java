/**   
* @Title: RegisterAction.java 
* @Package com.meiqi.openservice.action.register 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月8日 上午11:02:08 
* @version V1.0   
*/
package com.meiqi.openservice.action.login;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/** 
 * @ClassName: LoginVerifyAction 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08 
 *  
 */
@Service
public class LoginOutAction extends BaseAction{

    @Autowired
    private IMemcacheAction memcacheService;
    
	public Object loginOut(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
	 	
	    ResponseInfo respInfo=new ResponseInfo();
	    Map<String,Object> map=DataUtil.parse(repInfo.getParam(),Map.class);
        loginOut(request, response, map, memcacheService);
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
	    return respInfo;
	}

	public static void loginOut(HttpServletRequest request,HttpServletResponse response,Map<String,Object> map,IMemcacheAction memcacheService){
	    String userId=map.get("userId")==null?"":map.get("userId").toString();
        String type=map.get("type")==null?"":map.get("type").toString();
        String key="islogin_"+userId+"_"+type;
        String isAutoLoginKey="isAutologin_"+userId+"_"+type;
        String sessionIdLoginKey="login_"+request.getSession().getId();
        memcacheService.removeCache(key);
        memcacheService.removeCache(isAutoLoginKey);
        memcacheService.removeCache(sessionIdLoginKey);
        request.getSession().removeAttribute(key);
        //删除cookie
        Cookie uid = new Cookie("uid", userId);
        uid.setMaxAge(0);  
        uid.setPath("/");
        response.addCookie(uid);
	}
}

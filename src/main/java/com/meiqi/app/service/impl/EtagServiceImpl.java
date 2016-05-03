/**   
* @Title: EtagServiceImpl.java 
* @Package com.meiqi.app.service.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月6日 上午10:28:19 
* @version V1.0   
*/
package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.action.BaseAction;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.util.MD5Util;

/** 
 * @ClassName: EtagServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月6日 上午10:28:19 
 *  
 */
@Service
public class EtagServiceImpl implements EtagService {
	@Autowired
	private IMemcacheAction memcacheService;
	private static final Log LOG =  LogFactory.getLog("appEtag");
	
//	public boolean toUpdatEtag(HttpServletRequest request,HttpServletResponse response,String key) {
//		String oldEtag=request.getHeader("If-None-Match");
//    	String key_app_304_dirty=AppSysConfig.getValue("app_304_dirty");
//    	Object object=memcacheService.getCache(key_app_304_dirty);
//    	if(object!=null){
//    		Map<String,String> dirty=(Map<String,String>)object;
//    		String value=dirty.get(key);
//    		if(value!=null){
//    			if(oldEtag.equals(dirty.get(key))){
//    				//如果数据不是脏数据，返回304
//                	response.setStatus(response.SC_NOT_MODIFIED);
//            		response.setHeader("ETag",oldEtag);
//            		return true;
//            	}else{
//            		//如果数据是脏数据
//            		String cacheValue=Long.toString(System.currentTimeMillis());
//            		//更新缓存
//            		putEtagMarking(key,cacheValue);
//            		response.setHeader("ETag",cacheValue);
//            	}
//    		}else{
//    			//如果是第一次获取数据
//    			String time=Long.toString(System.currentTimeMillis());
//    			putEtagMarking(key,time);
//    			response.setHeader("ETag",time);
//    		}
//    	}else{
//    		//如果是第一次获取数据
//    		String time=Long.toString(System.currentTimeMillis());
//    		putEtagMarking(key,time);
//    		response.setHeader("ETag",time);
//    	}
//    	return false;
//	}
	
	public boolean toUpdatEtag1(HttpServletRequest request,HttpServletResponse response,String key,String currentValue) {
		//memcacheService.removeCache("app_304_dirty");
		if(currentValue==null){currentValue="";}
		String currentEtag=request.getHeader("If-None-Match");
    	String key_app_304_dirty=AppSysConfig.getValue("app_304_dirty");
    	// MD5加密
    	key_app_304_dirty = MD5Util.MD5(key_app_304_dirty);
    	Object object=memcacheService.getCache(key_app_304_dirty);
    	String data=MD5Util.MD5(currentValue+currentEtag);
    	LOG.info("Etag ,key="+key+",current data="+data+",currentEtag="+currentEtag);
    	if(object!=null && currentEtag!=null){
    		Map<String,String> cacheData=(Map<String,String>)object; 
    		String value=cacheData.get(MD5Util.MD5(BaseAction.getMemcacheKeyPerfix(request) + key));
    		LOG.info("Etag memcache data="+data);
    		if(value!=null){
    			if(data.equals(value)){
    				//如果数据不是脏数据，返回304
    				LOG.info("Etag return 304");
                	response.setStatus(response.SC_NOT_MODIFIED);
            		response.setHeader("ETag",currentEtag);
            		return true;
            	}else{
            		//如果数据是脏数据
            		String ETag=Long.toString(System.currentTimeMillis());
            		//更新缓存
            		putEtagMarking(request,key,currentValue+ETag);
            		response.setHeader("ETag",ETag);
            	}
    		}else{
    			//如果是第一次获取数据
    			String ETag=Long.toString(System.currentTimeMillis());
    			putEtagMarking(request,key,currentValue+ETag);
    			response.setHeader("ETag",ETag);
    		}
    	}else{
    		//如果是第一次获取数据
    		String ETag=Long.toString(System.currentTimeMillis());
    		LOG.info("Etag new:"+ETag);
    		putEtagMarking(request,key,currentValue+ETag);
    		response.setHeader("ETag",ETag);
    	}
    	return false;
	}
	
	public void putEtagMarking(HttpServletRequest request,String key,String cacheValue){
		if(cacheValue==null){cacheValue="";}
		String key_app_304_dirty=AppSysConfig.getValue("app_304_dirty");
		// MD5加密
        key_app_304_dirty = MD5Util.MD5(key_app_304_dirty);
    	Object object=memcacheService.getCache(key_app_304_dirty);
    	Map<String,String> map=null;
    	if(object==null){
    		map=new HashMap<String, String>();
    	}else{
    		map=(Map<String,String>)object;
    	}
    	// MD5加密
        key = MD5Util.MD5(BaseAction.getMemcacheKeyPerfix(request) + key);
    	String data=MD5Util.MD5(cacheValue);
    	map.put(key,data);
		boolean result=memcacheService.putCache(key_app_304_dirty, map);
		LOG.info("putEtagMarking key="+key+",data:"+data);
		LOG.info("putEtagMarking result:"+result);
	}
	
	
	public void putEtagMarking(String key,String cacheValue){
        if(cacheValue==null){cacheValue="";}
        String key_app_304_dirty=AppSysConfig.getValue("app_304_dirty");
        // MD5加密
        key_app_304_dirty = MD5Util.MD5(key_app_304_dirty);
        Object object=memcacheService.getCache(key_app_304_dirty);
        Map<String,String> map=null;
        if(object==null){
            map=new HashMap<String, String>();
        }else{
            map=(Map<String,String>)object;
        }
        // MD5加密
        key = MD5Util.MD5(key);
        String data=MD5Util.MD5(cacheValue);
        map.put(key,data);
        boolean result=memcacheService.putCache(key_app_304_dirty, map);
        LOG.info("putEtagMarking key="+key+",data:"+data);
        LOG.info("putEtagMarking result:"+result);
    }
}

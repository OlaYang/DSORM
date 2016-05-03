/**   
* @Title: SetAction.java 
* @Package com.meiqi.openservice.action
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月8日 上午11:02:08 
* @version V1.0   
*/
package com.meiqi.openservice.action;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.req.ActionReqInfo;
import com.meiqi.dsmanager.po.mushroom.req.ActionResult;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.util.ConfigFileUtil;
import com.meiqi.dsmanager.util.ListUtil;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.Base64;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.RsaKeyTools;
import com.meiqi.openservice.commons.util.StringUtils;

/**
 * 
* @ClassName: SetAction 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2015年12月9日 下午2:58:54 
*
 */
@Service
public class SetAction extends BaseAction{
    
    private static final Logger LOG = Logger.getLogger(SetAction.class);

    private static Properties  properties = new Properties();
    private static List<String> blacks;
    private static String dataUrlRedundantPrefix = "";
    @Autowired
    private IMushroomAction mushroomAction;
    @Autowired
    private IMemcacheAction memcacheService;
    static {
        try {
            //mushroom的黑名单，禁止直接调用setAction接口,以逗号隔开
            properties = ConfigFileUtil.propertiesReader("mushroomBlackconfig.properties");
            String refushCallServiceNameList=properties.getProperty("refushCallServiceNameList");
            if(StringUtils.isNotEmpty(refushCallServiceNameList)){
                String[] array=refushCallServiceNameList.split(",");
                blacks=Arrays.asList(array);
            }
            
            dataUrlRedundantPrefix = SysConfig.getValue("dataUrlRedundantPrefix");
        } catch (Exception e) {
            LogUtil.error("fail to find config file mushroomBlackconfig.properties", e);
        }
    }
    
    public Object set(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        String rsaverifyopen_set=RsaKeyTools.getRsaConfig("rsaverifyopen_set");
        if("1".equals(rsaverifyopen_set)){
            // RSA授权认证
            boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
            if (!rsaVerify) {
                ResponseInfo respInfo = new ResponseInfo();
                respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                return respInfo;
            }
        }
        String content = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(content, DsManageReqInfo.class);
        if(ListUtil.notEmpty(blacks)){
            //根据set操作的黑名单做检验
            if(dsReqInfo.getActions()!=null){
                //简单报文
                for(Action action:dsReqInfo.getActions()){
                    String serviceName=action.getServiceName();
                    if(blacks.contains(serviceName)){
                        ResponseInfo respInfo=new ResponseInfo();
                        respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                        respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                        return respInfo;
                    }
                }
            }else{
                //复杂报文
                String json = JSON.toJSONString(dsReqInfo.getParam());
                ActionReqInfo actionReqInfo =DataUtil.parse(json, ActionReqInfo.class);
                List<Action> actions=actionReqInfo.getActions();
                if(actions!=null){
                    for(Action action:actions){
                        String serviceName=action.getServiceName();
                        if(blacks.contains(serviceName)){
                            ResponseInfo respInfo=new ResponseInfo();
                            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                            return respInfo;
                        }
                    }
                }
            }
        }
        //根据前端传过来的key 清除memcache缓存
        Cookie[] cookies=request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                  String cookieName=cookie.getName();
                  if("removeMemCacheKey".equals(cookieName)){
                      String key=cookie.getValue();
                      boolean removeCacheResult=memcacheService.removeCache(key);
                      //LogUtil.info("set removeMemCacheKey:"+key+",result:"+removeCacheResult);
                      break;
                  }
            }
        }
        return mushroomAction.offer(dsReqInfo,request,response);
    }
    


    /**
     * 批量添加
     * 请求报文：
     * {
     *     "action": "setAction",
     *     "method": "batchAdd",
     *     "param": {
     *         "site_id": "0",
     *         "serviceName": "xxx",
     *         //"rows": [
     *         //    {"field1": "value11","field2": "value12"},
     *         //    {"field1": "value21","field2": "value22"}
     *         //],
     *         "transaction": 1,
     *         "base64Encoded": 0,
     *         "param": {
     *             "filed1": ["value11", "value12"],
     *             "filed2": ["value21", "value22"],...
     *         }
     *     }
     * }
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public Object batchAdd(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        String content = repInfo.getParam();
        JSONObject contentJson = JSONObject.parseObject(content);

        String serviceName = null;
        int transaction = 1;
        int base64Encoded = 0;

        // 获取site_id
        Integer site_id = 0;
        if (contentJson.containsKey("site_id")) {
            site_id = Integer.parseInt(contentJson.getString("site_id"));
        }
        
        if (!contentJson.containsKey("serviceName")) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ": serviceName");
            return respInfo;
        }
        serviceName = contentJson.getString("serviceName");

        if (contentJson.containsKey("transaction")) {
            transaction = Integer.parseInt(contentJson.getString("transaction"));
        }
        
        if (contentJson.containsKey("base64Encoded")) {
            base64Encoded = Integer.parseInt(contentJson.getString("base64Encoded"));
        }
        
        ActionRespInfo respInfo = new ActionRespInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        
        List<Action> actions = null;
        JSONObject fileds = contentJson.getJSONObject("param");
        for (String filed : fileds.keySet()) {
            JSONArray jsonValues = null;
            int size = 0;
            try {
                jsonValues = fileds.getJSONArray(filed);
                size = jsonValues.size();
            } catch (Exception e) {
                respInfo.setCode(DsResponseCodeData.REQINFO_NOT_RIGHT.code);
                respInfo.setDescription(DsResponseCodeData.REQINFO_NOT_RIGHT.description);
                return respInfo;
            }
            

            if (actions == null) {
                // 初始化批量添加数量的actions
                actions = new ArrayList<Action>(size);
                for (int i = 0; i < size; i++) {
                    Action action = new Action();
                    action.setType("C");
                    action.setSite_id(site_id);
                    action.setServiceName(serviceName);
                    actions.add(action);

                    Map<String, Object> set = new HashMap<String, Object>();
                    action.setSet(set);
                }
            } else {
                if (size != actions.size()) {
                    respInfo.setCode(DsResponseCodeData.REQINFO_NOT_RIGHT.code);
                    respInfo.setDescription(DsResponseCodeData.REQINFO_NOT_RIGHT.description);
                    return respInfo;
                }
            }
            
            for (int i = 0; i < size; i++) {
                // 为每个Action插入filed
                String value = jsonValues.get(i).toString();
                if (base64Encoded == 1) {
                    try {
                        value = new String(Base64.decode(value), "utf-8");
                        value = URLDecoder.decode(value, "utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.error("SetAction.batchAdd: " + e.getMessage());
                        respInfo.setCode(DsResponseCodeData.REQINFO_NOT_RIGHT.code);
                        respInfo.setDescription(DsResponseCodeData.REQINFO_NOT_RIGHT.description);
                        return respInfo;
                    }
                }
                
                // 特殊处理data_url字段
                if ("data_url".equals(filed)) {
                    value = value.replaceAll("\\\\", "\\\\\\\\");
                    if (value.startsWith(dataUrlRedundantPrefix)) {
                    	value = value.substring(dataUrlRedundantPrefix.length());
                    }
                }
                actions.get(i).getSet().put(filed, value);
            }
        }
        
     // 依次调用mushroom保存
        String allDesc = "";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("transaction", transaction);
        List<Action> actionsTmp = new ArrayList<Action>(1);
        actionsTmp.add(null);
        param.put("actions", actionsTmp);
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setFormat("json");
        actionReqInfo.setParam(param);
        for (Action action : actions) {
        	actionsTmp.set(0, action);
        	String result = mushroomAction.offer(actionReqInfo);
            JSONObject resultJson = JSONObject.parseObject(result);
            
            JSONArray jsonArray = (JSONArray)(resultJson.get("results"));
            if (jsonArray != null && jsonArray.size() != 0) {
            	ActionResult actionResult = DataUtil.parse(jsonArray.get(0).toString(), ActionResult.class);
            	respInfo.getResults().add(actionResult);
            }
            
			if (!DsResponseCodeData.SUCCESS.code.equals(resultJson.get("code"))) {
				LOG.error("batchAdd fail - result:" + result);
				String desc = resultJson.getString("description");
				
				if (StringUtils.isNotEmpty(desc)
						&& desc.contains("Duplicate entry")) {
					// 重复数据不算出错
					continue;
				}
				
				// 出错后继续保存后续数据
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				if (StringUtils.isNotEmpty(allDesc)) {
					allDesc += ", ";
				}
				allDesc += desc;
				respInfo.setDescription(allDesc);
			}
        }
        
        return respInfo;
    }
    
    /**
     * 支持批量写入，不支持$-1,$-2
    * @Title: simpleBetchSet 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return Object    返回类型 
    * @throws
     */
 public Object simpleBetchSet(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        // RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return respInfo;
        }
        String content = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(content, DsManageReqInfo.class);
        if(ListUtil.notEmpty(blacks)){
            //根据set操作的黑名单做检验
            if(dsReqInfo.getActions()!=null){
                //简单报文
                for(Action action:dsReqInfo.getActions()){
                    String serviceName=action.getServiceName();
                    if(blacks.contains(serviceName)){
                        ResponseInfo respInfo=new ResponseInfo();
                        respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                        respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                        return respInfo;
                    }
                }
            }else{
                //复杂报文
                String json = JSON.toJSONString(dsReqInfo.getParam());
                ActionReqInfo actionReqInfo =DataUtil.parse(json, ActionReqInfo.class);
                List<Action> actions=actionReqInfo.getActions();
                if(actions!=null){
                    for(Action action:actions){
                        String serviceName=action.getServiceName();
                        if(blacks.contains(serviceName)){
                            ResponseInfo respInfo=new ResponseInfo();
                            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                            return respInfo;
                        }
                    }
                }
            }
        }
        //根据前端传过来的key 清除memcache缓存
        Cookie[] cookies=request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                  String cookieName=cookie.getName();
                  if("removeMemCacheKey".equals(cookieName)){
                      String key=cookie.getValue();
                      boolean removeCacheResult=memcacheService.removeCache(key);
                      //LogUtil.info("set removeMemCacheKey:"+key+",result:"+removeCacheResult);
                      break;
                  }
            }
        }
        return mushroomAction.simpleBetchOffer(dsReqInfo,request,response);
    }
}

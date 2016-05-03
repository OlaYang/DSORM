package com.meiqi.openservice.action.jms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;



import org.apache.activemq.Message;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.action.impl.MemcacheActionImpl;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.jms.producer.TextMessageProducer;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.CollectionsUtils;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.util.MyApplicationContextUtil;


/**
 * 
* @ClassName: CRMInformationAction 
* @Description: TODO(将crm消息写入MQ，并定时读取消息入库) 
* @author fangqi
* @date 2015年10月26日 上午11:14:50 
*
 */
@Service("crmInformationAction")
public class CrmInformationAction extends BaseAction {
    
    private static final Log LOG =  LogFactory.getLog("crm");

    private static ApplicationContext context          = new ClassPathXmlApplicationContext(
            "spring/spring-common-jms.xml");
    private JmsTemplate               jmsTemplate;
    
    @Autowired
    private IDataAction     dataAction;
    
    @Autowired
    private IMushroomAction           mushroomAction;
    
    @Autowired
    private TextMessageProducer  removeCacheProducer;
    
    /**
     * 
    * @Title: setCrmInfoToMQ 
    * @Description: TODO(将CRM的消息写入到MQ中) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    @SuppressWarnings("unchecked")
    public String setCrmInfoToMQ(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
        ResponseInfo respInfo=new ResponseInfo();
        String code = "1";
        String description = "参数不能为空,数据写入MQ失败！";

        if("".equals(repInfo.getParam())){
            respInfo.setCode(code);
            respInfo.setDescription(description);
            return JSON.toJSONString(respInfo);
        }
        long currentTime = DateUtils.getSecond();
        Map<String, Object> map = DataUtil.parse(repInfo.getParam().replace("javaCurrentTime", String.valueOf(currentTime)), Map.class);
        JSONArray array = JSONArray.fromObject(map.get("actions"));
        if(null!=array&&array.size()>0){
            for(int i=0;i<array.size();i++){
                Map<String, Object> actionMap = DataUtil.parse(array.get(i).toString(),Map.class);
                if("test_ecshop_crm_client_table".equals(actionMap.get("serviceName").toString())){
                    Map<String, Object> set = (Map<String, Object>) actionMap.get("set");
                    set.put("ADD_time", currentTime);
                    actionMap.put("set",set);
                    array.set(i, actionMap);
                    map.put("actions", array);
                    break;
                }
            }
        }
        String queueName = "meiqi/CRMInfo";
        if(!StringUtils.isEmpty(SysConfig.getValue("siteId"))){
            queueName += "_"+SysConfig.getValue("siteId");
        }
        try{
            if(null!=map&&map.size()>0){

                LOG.info("Meiqi_MQ_CRM_SetInfo：发送消息["+DataUtil.toJSONString(map)+"]");
                removeCacheProducer.setDestination(queueName);
                removeCacheProducer.publish(map);
                code = DsResponseCodeData.SUCCESS.code;
                description = "CRM消息写入MQ"+DsResponseCodeData.SUCCESS.description;
            }else{
                description = repInfo.getParam() + description;
            }
        }catch(Exception e){
            code = "-1";
            description = e.getMessage();

            LOG.info("Meiqi_MQ_CRM_SetInfo_error:写入消息异常"+e.getMessage());
            e.printStackTrace();
        }
        respInfo.setCode(code);
        respInfo.setDescription(description);
        String json = JSON.toJSONString(respInfo);

        LOG.info("Meiqi_MQ_CRM_SetInfo:"+json);
        return json;
    }
    
    /**
     * 
    * @Title: getCrmInfoFromMQ 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param   参数说明 
    * @return void    返回类型 
    * @throws
     */
    public void getCrmInfoFromMQ(){
        long time = System.currentTimeMillis();
        jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
        jmsTemplate.setReceiveTimeout(1000);
        LOG.info("Meiqi_MQ_CRM_GetInfo:开始获取消息！");
        int num = 1500;
        if(!StringUtils.isEmpty(SysConfig.getValue("crmAccount"))){
            num = Integer.parseInt(SysConfig.getValue("crmAccount"));
        }
        String queueName = "meiqi/CRMInfo";
        if(!StringUtils.isEmpty(SysConfig.getValue("siteId"))){
            queueName += "_"+SysConfig.getValue("siteId");
        }
        ApplicationContext applicationContext=MyApplicationContextUtil.getContext();
        mushroomAction=(IMushroomAction)applicationContext.getBean("mushroomAction");
        int i=0;
        while (num > 0) {
            try {
                Message message = (Message) jmsTemplate.receive(queueName);
                if (message != null) {
                    ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
                    Map<String, Object> m = activeMQMapMessage.getContentMap();
                    if(null!=m&&m.size()>0){
                        LOG.info("Meiqi_MQ_CRM_GetInfo:Map消息内容["+DataUtil.toJSONString(m!=null?m:"")+"]");
                        DsManageReqInfo reqInfo = new DsManageReqInfo();
                        Object serviceName=m.get("servicename");
                        if(serviceName!=null){
                            reqInfo.setServiceName(serviceName.toString());
                        }else{
                            reqInfo.setServiceName(m.get("serviceName").toString());
                        }
                        Object tmp=m.get("actions");
                        if(null!=tmp){
                            JSONArray array = JSONArray.fromObject(tmp);
                            List<Action> actions = new ArrayList<Action>();
                            int size=array.size();
                            if(size==1){
                                Object o=array.get(0);
                                Action action = DataUtil.parse(o.toString(),Action.class);
                                actions.add(action);
                                reqInfo.setActions(actions);
                                String res = mushroomAction.offer(reqInfo);
                                ActionRespInfo respInfo = DataUtil.parse(res, ActionRespInfo.class);
                                if ("0".equals(respInfo.getCode())) {
                                    LOG.info("Meiqi_MQ_CRM_Mushroom_success:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入成功！");
                                } else {
                                    LOG.info("Meiqi_MQ_CRM_Mushroom_error:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入失败,[" + res + "]");
                                }
                            }else if(size==2){
                                Action crmBrowseTrackAction=null;
                                Action crmClientTableAction=null;
                                for(Object o:array){
                                    Action actionTmp = DataUtil.parse(o.toString(),Action.class);
                                    if("test_ecshop_crm_browse_track".equals(actionTmp.getServiceName())){
                                        crmBrowseTrackAction = actionTmp;
                                    }
                                    if("test_ecshop_crm_client_table".equals(actionTmp.getServiceName())){
                                        crmClientTableAction = actionTmp;
                                    }
                                }
                                actions.add(crmBrowseTrackAction);
                                reqInfo.setActions(actions);
                                String res = mushroomAction.offer(reqInfo);
                                ActionRespInfo respInfo = DataUtil.parse(res, ActionRespInfo.class);
                                if ("0".equals(respInfo.getCode()) && crmClientTableAction!=null) {
                                    LOG.info("Meiqi_MQ_CRM_Mushroom_success:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入成功！");
                                    //根据规则CRM_HSV1_Verification判断是否需要添加
                                    Map<String,Object> set = crmClientTableAction.getSet();
                                    dataAction = (IDataAction)applicationContext.getBean("dataAction");
                                    DsManageReqInfo ds = new DsManageReqInfo();
                                    ds.setServiceName("CRM_HSV1_Verification");
                                    Map<String,Object> map = new HashMap<String, Object>();
                                    map.put("user_name", set.get("user_name"));
                                    map.put("site_id", m.get("site_id"));
                                    ds.setParam(map);
                                    String result = dataAction.getData(ds);
                                    LOG.info("crm CRM_HSV1_Verification param:"+map+",result:"+result);
                                    RuleServiceResponseData responseData = null;
                                    responseData = DataUtil.parse(result, RuleServiceResponseData.class);
                                    if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                                        List<Map<String, String>> list=responseData.getRows();
                                        if(!CollectionsUtils.isNull(list)){
                                            Map<String, String> resultMap=list.get(0);
                                            String is_flag=resultMap.get("is_flag");
                                            LOG.info("crm CRM_HSV1_Verification param:"+map+",result:"+resultMap);
                                            if("0".equals(is_flag)){
                                                //需求 #17976
                                                DsManageReqInfo ds11 = new DsManageReqInfo();
                                                ds11.setServiceName("CRM_HSV1_Phone_Area");
                                                Map<String,Object> map11 = new HashMap<String, Object>();
                                                map11.put("phone", set.get("user_name"));
                                                map11.put("ip_source", set.get("ip_source"));
                                                ds11.setParam(map11);
                                                String result11 = dataAction.getData(ds11);
                                                LOG.info("crm CRM_HSV1_Phone_Area param:"+map11+",result:"+result11);
                                                RuleServiceResponseData responseData11 = null;
                                                responseData11 = DataUtil.parse(result11, RuleServiceResponseData.class);
                                                String province="";
                                                String city="";
                                                String district="";
                                                if (Constants.GetResponseCode.SUCCESS.equals(responseData11.getCode())) {
                                                    List<Map<String, String>> list11=responseData11.getRows();
                                                    if(!CollectionsUtils.isNull(list11)){
                                                        Map<String, String> resultMap11=list11.get(0);
                                                         province=resultMap11.get("province");
                                                         city=resultMap11.get("city");
                                                         district=resultMap11.get("district");
                                                    }
                                                }
                                                if(StringUtils.isNotEmpty(province)){
                                                    crmClientTableAction.getSet().put("province", province);
                                                }
                                                if(StringUtils.isNotEmpty(city)){
                                                    crmClientTableAction.getSet().put("city", city);
                                                }
                                                if(StringUtils.isNotEmpty(district)){
                                                    crmClientTableAction.getSet().put("district", district);
                                                }
                                                //如果不存在，那么插入crm_client_table
                                                DsManageReqInfo reqInfo1 = new DsManageReqInfo();
                                                reqInfo1.setServiceName("MUSH_Offer");
                                                List<Action> actions1 = new ArrayList<Action>();
                                                actions1.add(crmClientTableAction);
                                                reqInfo1.setActions(actions1);
                                                String res1 = mushroomAction.offer(reqInfo1);
                                                LOG.info("crm crmClientTableAction MUSH_Offer result:"+res1);
                                                ActionRespInfo respInfo1 = DataUtil.parse(res1, ActionRespInfo.class);
                                                if ("0".equals(respInfo1.getCode())) {
                                                    DsManageReqInfo ds1 = new DsManageReqInfo();
                                                    ds1.setServiceName("CRM_HSV1_autogeneration_task");
                                                    Map<String,Object> map1 = new HashMap<String, Object>();
                                                    map1.put("task_id", "0");
                                                    map1.put("user_name", set.get("user_name"));
                                                    map1.put("site_id", m.get("site_id"));
                                                    ds1.setParam(map1);
                                                    String res2 = dataAction.getData(ds1);
                                                    LOG.info("Meiqi_MQ_CRM_DataAction:调用规则[CRM_HSV1_autogeneration_task],返回消息[" + res2+ "]写入成功！");
                                                }
                                            }else if("1".equals(is_flag)){ //用户不存在时候
                                            	DsManageReqInfo ds1 = new DsManageReqInfo();
                                                ds1.setServiceName("CRM_HSV1_autogeneration_task");
                                                Map<String,Object> map1 = new HashMap<String, Object>();
                                                map1.put("task_id", "0");
                                                map1.put("user_name", set.get("user_name"));
                                                map1.put("site_id", m.get("site_id"));
                                                ds1.setParam(map1);
                                                String res2 = dataAction.getData(ds1);
                                                LOG.info("Meiqi_MQ_CRM_DataAction:调用规则[CRM_HSV1_autogeneration_task],返回消息[" + res2+ "]写入成功！");
                                            }
                                        }
                                    }
                                } else {
                                    LOG.info("Meiqi_MQ_CRM_Mushroom_error:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入失败,[" + res + "]");
                                }
                            }
                        }
                    }else{
                        LOG.info("Meiqi_MQ_CRM_GetInfo:Map消息内容为空！");
                    }
                    num--;
                    i++;
                } else {
                    num=0;
                    LOG.info("Meiqi_MQ_CRM_GetInfo:消息已为空！");
                    break;
                }
            } catch (Exception e) {
                LOG.info("Meiqi_MQ_CRM_GetInfo:" + e.getMessage());
            }
        }
        LOG.info("Meiqi_MQ_CRM_Mushroom_end:消息入库,消息数量["+i+"],结束时间["+DateUtils.formatDateToString(new Date())+"],耗时["+(System.currentTimeMillis()-time)+"ms]");
    }
    
    /**
     * 将微信扫码的Crm信息写入到MQ
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    @SuppressWarnings("unchecked")
	public String setWeixinScanCodeCrmInfoToMQ(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
        ResponseInfo respInfo=new ResponseInfo();
        String code = "1";
        String description = "参数不能为空,数据写入MQ失败！";
        if("".equals(repInfo.getParam())){
            respInfo.setCode(code);
            respInfo.setDescription(description);
            return JSON.toJSONString(respInfo);
        }
        long currentTime = DateUtils.getSecond();
        Map<String, Object> map = DataUtil.parse(repInfo.getParam().replace("javaCurrentTime", String.valueOf(currentTime)), Map.class);
        JSONArray array = JSONArray.fromObject(map.get("actions"));
        if(null == array){
        	respInfo.setCode(code);
        	respInfo.setDescription("传入的写入CRM消息为空！");
        	return JSON.toJSONString(respInfo);
        }
        String queueName = "meiqi/WeixinScanCodeCRMInfo";
        if(!StringUtils.isEmpty(SysConfig.getValue("siteId"))){
            queueName += "_"+SysConfig.getValue("siteId");
        }
        try{
            if(null!=map&&map.size()>0){
                LOG.info("Meiqi_MQ_WeixinScanCodeCRM_SetInfo：发送消息["+DataUtil.toJSONString(map)+"]");
                removeCacheProducer.setDestination(queueName);
                removeCacheProducer.publish(map);
                code = DsResponseCodeData.SUCCESS.code;
                description = "CRM消息写入MQ"+DsResponseCodeData.SUCCESS.description;
            }else{
                description = repInfo.getParam() + description;
            }
        }catch(Exception e){
            code = "-1";
            description = e.getMessage();
            LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo_error:写入消息异常"+e.getMessage());
            e.printStackTrace();
        }
        respInfo.setCode(code);
        respInfo.setDescription(description);
        String json = JSON.toJSONString(respInfo);
        LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:"+json);
        return json;
    }
    
    
    /**
     * 将MQ中微信扫码队列写入到数据库中
     */
    public void getWeinxinScanCodeCrmInfoFromMQ(){
        long time = System.currentTimeMillis();
        jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
        jmsTemplate.setReceiveTimeout(1000);
        LOG.info("Meiqi_MQ_WeixinScanCodeCRM_GetInfo:开始获取消息！");
        int num = 1500;
        if(!StringUtils.isEmpty(SysConfig.getValue("crmAccount"))){
            num = Integer.parseInt(SysConfig.getValue("crmAccount"));
        }
        String queueName = "meiqi/WeixinScanCodeCRMInfo";
        if(!StringUtils.isEmpty(SysConfig.getValue("siteId"))){
            queueName += "_"+SysConfig.getValue("siteId");
        }
        ApplicationContext applicationContext=MyApplicationContextUtil.getContext();
        mushroomAction=(IMushroomAction)applicationContext.getBean("mushroomAction");
        int i=0;
        while (num > 0) {
            try {
                Message message = (Message) jmsTemplate.receive(queueName);
                if (message != null) {
                    ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
                    Map<String, Object> m = activeMQMapMessage.getContentMap();
                    if(null!=m&&m.size()>0){
                        LOG.info("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:Map消息内容["+DataUtil.toJSONString(m!=null?m:"")+"]");
                        DsManageReqInfo reqInfo = new DsManageReqInfo();
                        Object serviceName=m.get("servicename");
                        String cacheKey = m.get("cacheKey") == null?"":m.get("cacheKey").toString();
                        if(serviceName!=null){
                            reqInfo.setServiceName(serviceName.toString());
                        }else{
                            reqInfo.setServiceName(m.get("serviceName").toString());
                        }
                        Object tmp=m.get("actions");
                        if(null!=tmp){
                            JSONArray array = JSONArray.fromObject(tmp);
                            List<Action> actions = new ArrayList<Action>();
                            int size=array.size();
                            if(size==1){
                                Object o=array.get(0);
                                Action action = DataUtil.parse(o.toString(),Action.class);
                                actions.add(action);
                                reqInfo.setActions(actions);
                                String res = mushroomAction.offer(reqInfo);
                                LOG.info("Meiqi_MQ_WeixinScanCodeCRM set param is: "+JSONObject.parse(reqInfo.toString()));
                                LOG.info("Meiqi_MQ_WeixinScanCodeCRM set result is: "+JSONObject.parse(res));
                                ActionRespInfo respInfo = DataUtil.parse(res, ActionRespInfo.class);
                                if ("0".equals(respInfo.getCode())) {
                                    LOG.info("Meiqi_MQ_CRM_Mushroom_success:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入成功！");
                                } else {
                                    LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入失败,[" + res + "]");
                                }
                            }else if(size==2){
                            	MemcacheActionImpl memcacheService = (MemcacheActionImpl) applicationContext.getBean("memcacheActionImpl");
                            	JSONObject cache = JSONObject.parseObject(memcacheService.getCache(cacheKey).toString());
                            	LOG.info("Meiqi_MQ_WeixinScanCodeCRM cache_key is: "+cacheKey+" cacahe_value is: "+cache);
                            	if(null == cache){
                            		LOG.error("从缓存里面取值为空,缓存的key为: "+cacheKey);
                            		return;
                            	}
                                Action crmBrowseTrackAction=null;
                                Action crmMicroInfo=null;
                                for(Object o:array){
                                    Action actionTmp = DataUtil.parse(o.toString(),Action.class);
                                    if("test_ecshop_crm_browse_track".equals(actionTmp.getServiceName())){
                                        crmBrowseTrackAction = actionTmp;
                                    }
                                    if("test_ecshop_crm_micro_info".equals(actionTmp.getServiceName())){
                                    	crmMicroInfo = actionTmp;
                                    }
                                }
                                Map<String,Object> setMap = crmBrowseTrackAction.getSet();
                                for (Map.Entry<String, Object> entry : cache.entrySet()){
                                	String key = entry.getKey();
                                	String value = entry.getValue().toString();
                                	setMap.put(key, value);
                                }
                                actions.add(crmBrowseTrackAction);
                                reqInfo.setActions(actions);
                                String res = mushroomAction.offer(reqInfo);
                                LOG.info("Meiqi_MQ_WeixinScanCodeCRM set param is: "+JSONObject.toJSONString(reqInfo));
                                LOG.info("Meiqi_MQ_WeixinScanCodeCRM set result is: "+res);
                                String first_change_id = "";
                                ActionRespInfo respInfo = DataUtil.parse(res, ActionRespInfo.class);
                                if ("0".equals(respInfo.getCode()) && crmMicroInfo!=null) {
                                	JSONObject jsonObject = JSONObject.parseObject(res);
                                	com.alibaba.fastjson.JSONArray parseArray = com.alibaba.fastjson.JSONArray.parseArray(jsonObject.getString("results"));
                                	first_change_id = parseArray.getJSONObject(0).get("generateKey").toString();
                                    LOG.info("Meiqi_MQ_WeixinScanCodeCRM_Mushroom_success:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入成功,result:["+DataUtil.toJSONString(respInfo)+"]");
                                    //根据规则CRM_HSV1_Verification判断是否需要添加
                                    Map<String,Object> setMap1 = crmMicroInfo.getSet();
                                    dataAction = (IDataAction)applicationContext.getBean("dataAction");
                                    DsManageReqInfo ds = new DsManageReqInfo();
                                    ds.setServiceName("CRM_HSV1_Verification");
                                    Map<String,Object> map = new HashMap<String, Object>();
                                    map.put("union_id", setMap1.get("union_id"));
                                    map.put("site_id", m.get("site_id"));
                                    ds.setParam(map);
                                    String result = dataAction.getData(ds);
                                    LOG.info("crm CRM_HSV1_Verification param:"+JSONObject.toJSONString(ds)+",result:"+result);
                                    RuleServiceResponseData responseData = null;
                                    responseData = DataUtil.parse(result, RuleServiceResponseData.class);
                                    if (Constants.GetResponseCode.ERROR.equals(responseData.getCode())) {
                                    	LOG.error("getWeinxinScanCodeCrmInfoFromMQ CRM_HSV1_Verification 查询错误 param is: "+JSONObject.toJSONString(ds)+",result:"+result);
                                    	return;
                                    }
                                    List<Map<String, String>> mapList = responseData.getRows();
                                    if(null == mapList){
                                    	LOG.error("getWeinxinScanCodeCrmInfoFromMQ CRM_HSV1_Verification 查询无数据 param is: "+JSONObject.toJSONString(ds)+",result:"+result);
                                    	return;
                                    }
                                    LOG.info("getWeinxinScanCodeCrmInfoFromMQ CRM_HSV1_Verification param is: "+JSONObject.toJSONString(ds)+",result:"+result);
                                    String is_exist = mapList.get(0).get("is_exist");
                                    String cookie = cache.get("cookie").toString();
                                    if("0".equals(is_exist)){
                                    	crmMicroInfo.setType("C");
                                    	setMap1.put("cookie", cookie);
                                    	setMap1.put("first_change_id", first_change_id);
                                    	crmMicroInfo.setSet(setMap1);
                                    }else if("1".equals(is_exist)){
                                    	crmMicroInfo.setType("U");
                                    	crmMicroInfo.setSet(setMap1);
                                    	crmMicroInfo.setKey("union_id="+setMap1.get("union_id"));
                                    }
                                    List<Action> actions1 = new ArrayList<Action>();
                                    actions1.add(crmMicroInfo);
                                    DsManageReqInfo reqInfo1 = new DsManageReqInfo();
                                    reqInfo1.setActions(actions1);
                                    String result1 = mushroomAction.offer(reqInfo1);
                    				JSONObject job = JSONObject.parseObject(result1);
                    				LOG.info("getWeinxinScanCodeCrmInfoFromMQ test_ecshop_crm_micro_info set param is: "+JSONObject.toJSONString(reqInfo1));
                    				LOG.info("getWeinxinScanCodeCrmInfoFromMQ test_ecshop_crm_micro_info set result is: "+result1);
                    				if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
                    					LOG.info("getWeinxinScanCodeCrmInfoFromMQ test_ecshop_crm_micro_info set param is: "+JSONObject.toJSONString(reqInfo1));
                        				LOG.info("getWeinxinScanCodeCrmInfoFromMQ test_ecshop_crm_micro_info set result is: "+result1);
                    					return;
                    			    }
                                } else {
                                    LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:消息[" + DataUtil.toJSONString(reqInfo)+ "]写入失败,[" + res + "]");
                                }
                            }
                        }
                    }else{
                        LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:Map消息内容为空！");
                    }
                    num--;
                    i++;
                } else {
                    num=0;
                    LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:消息已为空！");
                    break;
                }
            } catch (Exception e) {
                LOG.error("Meiqi_MQ_WeixinScanCodeCRM_SetInfo:" + e.getMessage());
            }
        }
        LOG.info("Meiqi_MQ_CRM_Mushroom_end:消息入库,消息数量["+i+"],结束时间["+DateUtils.formatDateToString(new Date())+"],耗时["+(System.currentTimeMillis()-time)+"ms]");
    }
}

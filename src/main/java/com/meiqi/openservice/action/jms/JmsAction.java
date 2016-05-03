package com.meiqi.openservice.action.jms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.Message;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.jms.producer.TextMessageProducer;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.ClearCacheAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @ClassName: JmsAction
 * @Description: TODO(记录ipad用户行为)
 * @author fangqi
 * @date 2015年12月9日 下午1:14:10
 *
 */
@Service
public class JmsAction extends BaseAction {

    private static final Log                             LOG          = LogFactory.getLog("ipad");
    @Autowired
    private IMemcacheAction                              memcacheService;
    @Autowired
    private TextMessageProducer                          removeCacheProducer;

    private static ApplicationContext                    context      = new ClassPathXmlApplicationContext(
                                                                              "spring/spring-common-jms.xml");

    private JmsTemplate                                  jmsTemplate;

    @Autowired
    private IMushroomAction                              mushroomAction;

    @Autowired
    private ThreadHelper                                 indexTheadHelper;

    @Autowired
    private IDataAction                                  dataAction;

    @Autowired
    private ClearCacheAction                             clearCacheAction;

    private static Map<String, Set<Map<String, String>>> ipadCacheMap = new HashMap<String, Set<Map<String, String>>>();



    public Object dsormCacheProduce(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        ResponseInfo respInfo = new ResponseInfo();
        removeCacheProducer.publish(repInfo.getParam());
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return respInfo;
    }



    /**
     * 
     * @Title: clearIpadUserCache
     * @Description: TODO(清楚缓存)
     * @param @param request
     * @param @param response
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public String reloadIpadUserCache(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        ResponseInfo result = new ResponseInfo();
        ipadCacheMap.clear();
        try {
            LOG.info("reloadIpadUserCache[clearCacheAction_start]:开始清除[BUV1_Ipid_log]规则缓存,时间["
                    + DateUtils.formatDateToString(new Date()) + "]");
            Map<String, Object> serviceMap = new HashMap<String, Object>();
            serviceMap.put("serviceName", "BUV1_Ipid_log");
            repInfo.setParam(JSONObject.toJSONString(serviceMap));
            String clearResult = clearCacheAction.clear(request, response, repInfo);

            LOG.info("reloadIpadUserCache[clearCacheAction_end]:清楚[BUV1_Ipid_log]规则缓存结束[" + clearResult + "],时间["
                    + DateUtils.formatDateToString(new Date()) + "]");
            LOG.info("reloadIpadUserCache_start:开始重载[BUV1_Ipid_log]规则,时间["
                    + DateUtils.formatDateToString(new Date()) + "]");

            Map<String, Object> reqMap = new HashMap<String, Object>();
            DsManageReqInfo dsReqInfo = new DsManageReqInfo();
            dsReqInfo.setServiceName("BUV1_Ipid_log");
            dsReqInfo.setParam(reqMap);
            ResponseInfo respInfo = DataUtil.parse(dataAction.getData(dsReqInfo), ResponseInfo.class);
            List<Map<String, String>> list = respInfo.getRows();
            if (null != list && list.size() > 0) {
                for (Map<String, String> m : list) {
                    String key = m.get("event_action") + "_" + m.get("event_method");
                    Set<Map<String, String>> valSet = ipadCacheMap.get(key);
                    if (null == valSet) {
                        valSet = new HashSet<Map<String, String>>();
                    }
                    valSet.add(m);
                    ipadCacheMap.put(key, valSet);
                    LOG.info("reloadIpadUserCache_data：[BUV1_Ipid_log]重载数据[" + JSONObject.toJSONString(m) + "]");
                }
            }
            result.setCode("0");
            result.setDescription("reloadIpadUserCache：[BUV1_Ipid_log]数据重载成功！");
            LOG.info("reloadIpadUserCache_end:重载[BUV1_Ipid_log]规则结束,时间[" + DateUtils.formatDateToString(new Date())
                    + "]");
        } catch (Exception e) {
            result.setCode("1");
            result.setDescription("reloadIpadUserCache：[BUV1_Ipid_log]数据重载失败[" + e.getMessage() + "]");
            LOG.info("reloadIpadUserCache_error：[BUV1_Ipid_log]数据重载失败[" + e.getMessage() + "],时间["
                    + DateUtils.formatDateToString(new Date()) + "]");
        }
        return JSONObject.toJSONString(result);
    }



    /**
     * 
     * @Title: recordIpadUserAction
     * @Description: TODO(记录Ipad用户行为)
     * @param @param uuid
     * @param @param repInfo
     * @param @param eventId
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public void recordIpadUserAction(HttpServletRequest request, String decodeContent, String respStr) {
        String uuid = request.getHeader("deviceId");
        if (null != uuid && !"".equals(uuid)) {
            String city_id = request.getHeader("cityId");
            if(null==city_id||"".equals(city_id)){
                city_id = "0";
            }
            RecordUserAction thread = new RecordUserAction(uuid, decodeContent, city_id);
            indexTheadHelper.execute(thread);
        }
    }



    /**
     * 
     * @Title: readIpadUserActionInfo
     * @Description: TODO(读取MQ中的用户行为信息)
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    public void readIpadUserActionInfo() {
        jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
        jmsTemplate.setReceiveTimeout(1000);
        Date date = new Date();
        LOG.info("Meiqi_MQ_Ipad_read_start:开始获取消息,时间[" + DateUtils.formatDateToString(date) + "]");
        ApplicationContext applicationContext = MyApplicationContextUtil.getContext();
        mushroomAction = (IMushroomAction) applicationContext.getBean("mushroomAction");
        int num = 5000;
        int count = 0;
        while (num > 0) {
            try {
                Message message = (Message) jmsTemplate.receive("meiqi/IpadUserAction");
                if (message != null) {
                    ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
                    Map<String, Object> m = activeMQMapMessage.getContentMap();
                    if (null != m && m.size() > 0) {
                        LOG.info("Meiqi_MQ_Ipad_GetInfo:Map消息内容[" + DataUtil.toJSONString(m != null ? m : "") + "]");
                        DsManageReqInfo reqInfo = new DsManageReqInfo();
                        reqInfo.setServiceName("MUSH_Offer");
                        List<Action> actions = new ArrayList<Action>();
                        Action action = new Action();
                        action.setType("C");
                        action.setServiceName("meiqiserver_ipad_user_detail");
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("event_info", m.get("event_info").toString());
                        map.put("UUID", m.get("UUID").toString());
                        map.put("event_id", m.get("event_id").toString());
                        map.put("event_time", m.get("event_time").toString());
                        map.put("city_id", m.get("city_id").toString());//城市ID
                        action.setSet(map);
                        actions.add(action);
                        reqInfo.setActions(actions);
                        String res1 = mushroomAction.offer(reqInfo);
                        ActionRespInfo respInfo = DataUtil.parse(res1, ActionRespInfo.class);
                        if ("0".equals(respInfo.getCode())) {
                            LOG.info("Meiqi_MQ_Ipad_Mushroom_success:消息[" + DataUtil.toJSONString(reqInfo)
                                    + "]写入成功！");
                        } else {
                            LOG.info("Meiqi_MQ_Ipad_Mushroom_error:消息[" + DataUtil.toJSONString(reqInfo)
                                    + "]写入失败,[" + res1 + "]");
                        }
                    } else {
                        LOG.info("Meiqi_MQ_Ipad_GetInfo:Map消息内容为空！");
                    }
                    num--;
                    count++;
                } else {
                    num = 0;
                    LOG.info("Meiqi_MQ_Ipad_read_empty:消息已为空,时间[" + DateUtils.formatDateToString(new Date()) + "]");
                    break;
                }
            } catch (Exception e) {
                LOG.info("Meiqi_MQ_Ipad_read_exception:" + e.getMessage() + ",时间["
                        + DateUtils.formatDateToString(new Date()) + "]");
                num = 0;
            }
        }
        LOG.info("Meiqi_MQ_Ipad_read_end:[" + count + "]条消息获取、写入数据库结束,时间["
                + DateUtils.formatDateToString(new Date()) + "],共耗时[" + (System.currentTimeMillis() - date.getTime())
                + "]");
    }



    /**
     * 
     * @Title: dealMQInfo
     * @Description: TODO(处理用户行为消息)
     * @param @param list 参数说明
     * @return void 返回类型
     * @throws
     */
    public void dealMQInfo(List<Map<String, Object>> list) {
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        reqInfo.setServiceName("MUSH_Offer");
        List<Action> actions = new ArrayList<Action>();
        Action action = null;
        for (Map<String, Object> map : list) {
            new ArrayList<Action>();
            action = new Action();
            action.setType("C");
            action.setServiceName("meiqiserver_ipad_user_detail");// 写入层
            map.put("event_info", map.get("event_info").toString());
            map.put("UUID", map.get("UUID").toString());
            map.put("event_id", map.get("event_id").toString());
            action.setSet(map);
            actions.add(action);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        reqInfo.setParam(param);
        ApplicationContext applicationContext = MyApplicationContextUtil.getContext();
        mushroomAction = (IMushroomAction) applicationContext.getBean("mushroomAction");
        Date date = new Date();
        LOG.info("Meiqi_MQ_Ipad_insert_start:用户行为消息开始写入,开始时间[" + DateUtils.formatDateToString(date) + "]");
        String respStr = mushroomAction.offer(reqInfo);
        LOG.info("Meiqi_MQ_Ipad_insert_end:[" + actions.size() + "]条用户行为消息写入完成,结束时间["
                + DateUtils.formatDateToString(new Date()) + ",写入耗时" + (System.currentTimeMillis() - date.getTime())
                + "]");
    }

    /**
     * 
     * @ClassName: RecordUserAction
     * @Description: TODO(抽象类 异步执行用户行为写入mq)
     * @author fangqi
     * @date 2015年10月29日 下午8:00:28
     *
     */
    private class RecordUserAction implements ThreadCallback {
        private String  deviceId;
        private String  decodeContent;
        private RepInfo repInfo;
        private String  city_id;



        public RecordUserAction(String deviceId, String decodeContent, String city_id) {
            this.deviceId = deviceId;
            this.decodeContent = decodeContent;
            this.city_id = city_id;
        }



        @Override
        public void run() {
            try {
                LOG.info("Meiqi_MQ_Ipad_Info：用户行为消息[" + decodeContent + "]");
                repInfo = DataUtil.parse(decodeContent, RepInfo.class);
                String eventId = getUUID(repInfo);
                if (!eventId.equals("")) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("UUID", deviceId);// 设备UUID
                    map.put("event_id", eventId);// 事件id
                    map.put("city_id", city_id);//城市ID
                    if (repInfo.getParam().equals("") || null == repInfo.getParam()) {
                        LOG.info("Meiqi_MQ_Ipad_Info：用户行为消息为空,时间[" + DateUtils.formatDateToString(new Date()) + "]");
                        return;
                    }
                    map.put("event_info", decodeContent);// 用户事件内容
                    map.put("event_time", System.currentTimeMillis() / 1000);// 事件发生时间
                    LOG.info("Meiqi_MQ_Ipad_record_start：发送用户行为消息,时间[" + DateUtils.formatDateToString(new Date())
                            + "]");
                    removeCacheProducer.setDestination("meiqi/IpadUserAction");
                    removeCacheProducer.publish(map);
                }
            } catch (Exception e) {
                LOG.info("Meiqi_MQ_Ipad_record_exception：用户行为写入MQ异常[" + e.getMessage() + "],时间["
                        + DateUtils.formatDateToString(new Date()) + "]");
            }
        }



        private String getUUID(RepInfo repInfo) {
            if (null == ipadCacheMap || ipadCacheMap.size() == 0) {
                Map<String, Object> reqMap = new HashMap<String, Object>();
                DsManageReqInfo dsReqInfo = new DsManageReqInfo();
                dsReqInfo.setServiceName("BUV1_Ipid_log");// 规则名称
                dsReqInfo.setParam(reqMap);
                ResponseInfo respInfo = DataUtil.parse(dataAction.getData(dsReqInfo), ResponseInfo.class);
                List<Map<String, String>> list = respInfo.getRows();
                if (null != list && list.size() > 0) {
                    for (Map<String, String> m : list) {
                        String key = m.get("event_action") + "_" + m.get("event_method");
                        Set<Map<String, String>> valSet = ipadCacheMap.get(key);
                        if (null == valSet) {
                            valSet = new HashSet<Map<String, String>>();
                        }
                        valSet.add(m);
                        ipadCacheMap.put(key, valSet);
                    }
                }
            }
            String reqStr = repInfo.getParam().replaceAll(" ", "");
            String key = repInfo.getAction() + "_" + repInfo.getMethod();
            Set<Map<String, String>> set = ipadCacheMap.get(key);
            if (set != null) {
                for (Map<String, String> m : set) {
                    String str = m.get("event_condition").replaceAll(" ", "");
                    String[] arr = str.split("☆");
                    boolean flag = true;
                    for (int i = 0; i < arr.length; i++) {
                        if (reqStr.indexOf(arr[i]) == -1) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        return m.get("event_id");
                    }
                }
            }
            return "";
        }

    }

}

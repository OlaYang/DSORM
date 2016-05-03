/**   
* @Title: CrmClientTableThread.java 
* @Package com.meiqi.openservice.threads 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2016年2月22日 下午4:20:05 
* @version V1.0   
*/
package com.meiqi.openservice.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.openservice.action.jms.CrmInformationAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.util.MyApplicationContextUtil;

/** 
 * @ClassName: CrmClientTableThread 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2016年2月22日 下午4:20:05 
 *  
 */
public class CrmClientTableAndBrowseTrackThread implements ThreadCallback {

    private Map<String,Object> params;
    
    public CrmClientTableAndBrowseTrackThread( Map<String,Object> params){
        this.params=params;
    }
    /*
    * Title: run
    * Description:  
    * @see com.meiqi.thread.ThreadCallback#run() 
    */
    @Override
    public void run() {
        IMushroomAction mushroomAction=(IMushroomAction)MyApplicationContextUtil.getBean("mushroomAction");
        Object user_name=params.get("user_name");//手机号
        Object ADD_time=params.get("ADD_time");//当前时间戳(10位)
        Object ipMsg=params.get("ipMsg");//城市名称
        String ip_source="";//城市名称
        if(ipMsg!=null && StringUtils.isNotEmpty(ipMsg.toString())){
            Map<String,Object> headerMap=(Map<String,Object>)ipMsg;
            ip_source=headerMap.get("city").toString();
        }
        Object collect_event=params.get("collect_event");//采集事件
        Object collect_content=params.get("collect_content");//采集内容
        Object type=params.get("type");//类型 1 着陆 2 转化
        Object cookie=params.get("cookie");//记录user_id
        Object header=params.get("header");
        Map<String,Object> headerMap=(Map<String,Object>)header;
        String from_plate="";//(来源平台 1 PC端 2 M站 3 APP-Android 4 iPad 5 APP-iOS 6微信)
        if(headerMap!=null){
            String plat=headerMap.get("plat").toString();
            if(ContentUtils.PLAT_ANDROID.equals(plat)){
                from_plate="3";
            }else  if(ContentUtils.PLAT_IPHONE.equals(plat)){
                from_plate="5";
            }else if(ContentUtils.PLAT_IPAD.equals(plat)){
                String UserAgent=headerMap.get("User-Agent").toString();
                if(UserAgent.contains("StoreAide")){
                    from_plate="7";
                }else if(UserAgent.contains("YouJiaGou")){
                    from_plate="4";
                }
            }
        }
        Object collect_place=params.get("collect_place");//(采集位置)：固定记为“商品详情页-到店体验 ”
        
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        List<Action> actions = new ArrayList<Action>();
        Action action = new Action();
        action.setType("C");
        action.setServiceName("test_ecshop_crm_client_table");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_name", user_name);//手机号
        set.put("ADD_time", ADD_time);//当前时间戳（10位）
        set.put("ip_source", ip_source);//IP定位城市名
        action.setSet(set);
        actions.add(action);
        
        Action action1 = new Action();
        action1.setType("C");
        action1.setServiceName("test_ecshop_crm_browse_track");
        Map<String, Object> set1 = new HashMap<String, Object>();
        set1.put("collect_event", collect_event);//采集事件
        set1.put("collect_content", collect_content);//采集内容
        set1.put("type", type);//类型 1 着陆 2 转化
        set1.put("add_time", ADD_time);//类型 1 着陆 2 转化
        //set1.put("cookie", cookie);//cookie信息,记录user_id
        set1.put("phone", user_name);//客户手机号
        set1.put("from_plate", from_plate);//客户手机号
        set1.put("collect_place", collect_place);//采集位置
        set1.put("ip_source", ip_source);//ip来源
        action1.setSet(set1);
        actions.add(action1);
        
        actionReqInfo.setActions(actions);
       
        String json=JSONObject.toJSONString(actionReqInfo);
        RepInfo repInfo=new RepInfo();
        repInfo.setParam(json);
        
        CrmInformationAction crmInformationAction=(CrmInformationAction)MyApplicationContextUtil.getBean("crmInformationAction");
        crmInformationAction.setCrmInfoToMQ(null, null, repInfo);
    }
}

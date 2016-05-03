/*
 * File name: CollectAction.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年9月10日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.action.javabin.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;

/**
 * <class description>
 *
 * @author: zhouyongxiong
 * @version: 1.0, 2015年9月10日
 */
@Service
public class BonusAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(BonusAction.class);

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;

    public Object addBonusSequenceForPcOrder(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        
        Map<String, String> map = DataUtil.parse(repInfo.getParam(), Map.class);
        String code=map.get("code");
        String user_id=map.get("user_id");
        String order_sn=map.get("order_sn");
        
        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        try {
            if(StringUtils.isEmpty(code)){
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("code不能为空");
                return respInfo;
            }
            if(StringUtils.isEmpty(user_id)){
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("user_id不能为空");
                return respInfo;
            }
            if(StringUtils.isEmpty(order_sn)){
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("order_sn不能为空");
                return respInfo;
            }
            String bonus_id="";
            String cellPhone="";
            String get_id="";
            
            String serviceName_esc_pay_log = "IPAD_HSV1_discount_new";
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName(serviceName_esc_pay_log);
            serviceReqInfo.setNeedAll("1");
            RuleServiceResponseData responseData = null;
            String data1 =dataAction.getData(serviceReqInfo,"");
            responseData = DataUtil.parse(data1, RuleServiceResponseData.class);
            if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                List<Map<String, String>> list=responseData.getRows();
                Map<String, String> resultMap=list.get(0);
                bonus_id=resultMap.get("bonus_id");
            }

            DsManageReqInfo serviceReqInfo1 = new DsManageReqInfo();
            serviceReqInfo1.setServiceName("IPAD_HSV1_code_validated");
            Map<String, Object> queryParam = new HashMap<String, Object>();
            queryParam.put("code", code);
            serviceReqInfo1.setParam(queryParam);
            serviceReqInfo1.setNeedAll("1");

            String data = dataAction.getData(serviceReqInfo1, "");
            RuleServiceResponseData responseData1 = DataUtil.parse(data, RuleServiceResponseData.class);
            if (DsResponseCodeData.SUCCESS.code.equals(responseData1.getCode()) && responseData1.getRows().size() != 0) {
                Map<String, String> jsonMap = responseData1.getRows().get(0);
                if ("1".equals(jsonMap.get("is_validated"))) {
                    get_id=jsonMap.get("use_user_id");
                    cellPhone=jsonMap.get("phone");
                }else{
                    respInfo.setCode(DsResponseCodeData.ERROR.code);
                    respInfo.setCode("code不正确！");
                    return respInfo;
                }
            }
            if(StringUtils.isNotEmpty(get_id)){
                String order_id="";
                DsManageReqInfo serviceReqInfo2 = new DsManageReqInfo();
                serviceReqInfo2.setServiceName("COM_BUV1_orderinfo");
                Map<String, Object> queryParam2 = new HashMap<String, Object>();
                queryParam2.put("ordersn", order_sn);
                serviceReqInfo2.setParam(queryParam2);
                serviceReqInfo2.setNeedAll("1");
                String data2 = dataAction.getData(serviceReqInfo2, "");
                RuleServiceResponseData responseData2 = DataUtil.parse(data2, RuleServiceResponseData.class);
                if (DsResponseCodeData.SUCCESS.code.equals(responseData2.getCode()) && responseData2.getRows().size() != 0) {
                    Map<String, String> jsonMap = responseData2.getRows().get(0);
                    order_id=jsonMap.get("order_id");
                }
                if(StringUtils.isEmpty(order_id)){
                    respInfo.setCode(DsResponseCodeData.ERROR.code);
                    respInfo.setDescription("订单编号order_sn="+order_sn+"对应的订单不存在");
                    return respInfo;
                }
                Map<String,Object> paramTmp=new HashMap<String, Object>();
                paramTmp.put("bonus_id", bonus_id);
                paramTmp.put("user_id", user_id);
                paramTmp.put("cell_phone", cellPhone);
                paramTmp.put("order_id", order_id);
                paramTmp.put("get_id", get_id);
                paramTmp.put("create_time", "$UnixTime");
                paramTmp.put("get_time", "$UnixTime");
                paramTmp.put("user_time", "$UnixTime");
                paramTmp.put("bonus_status", "1");//1 已使用
                paramTmp.put("get_type", "1");//前台领取
                paramTmp.put("sequence_type", "1");//折扣
                String operateResult="";
                operateResult=addBonusSequence(paramTmp,0,operateResult);
                if(!"".equals(operateResult)){
                    respInfo.setCode(DsResponseCodeData.ERROR.code);
                    respInfo.setDescription(operateResult);
                }
            }
        } catch (Exception e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description);
        }
        
        return respInfo;
    }

    public String addBonusSequence(Map<String,Object> set,int i,String operateResult){
        
        String sequence_sn = CodeUtils.getBonusCode();
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String serviceName = "test_ecshop_ecs_bonus_sequence";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        set.put("sequence_sn", sequence_sn);//添加序列号
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse = null;
        String res1 = mushroomAction.offer(actionReqInfo);
        actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
        i++;
        int try_num=10;
        if (!Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
            operateResult=res1;
            if(i<=try_num){
                //如果添加失败，那么重试10次
                addBonusSequence(set,i,operateResult);
            }
        }else{
            operateResult="";
        }
        return operateResult;
    }

}

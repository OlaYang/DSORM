/**
 * @Title: RegisterAction.java
 * @Package com.meiqi.openservice.action.register
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * @version V1.0
 */
package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.action.SmsCodeAction;
import com.meiqi.app.action.UsersAction;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;

/**
 * @ClassName: RegisterAction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * 
 */
@Service
public class CodeAction extends BaseAction {

    @Autowired
    private UsersAction     userAction;
    @Autowired
    private IMushroomAction mushroomAction;
    @Autowired
    private IDataAction     dataAction;
    @Autowired
    private ThreadHelper    indexTheadHelper;
    @Autowired
    private SmsCodeAction   smsCodeAction;



    // 获取手机验证码
    /*public String getCode(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        Map<String, String> map = DataUtil.parse(repInfo.getParam(), Map.class);
        SMSCode smsCode = new SMSCode();
        smsCode.setPhone(map.get("phone"));
        smsCode.setType(Byte.valueOf(map.get("codeType")));
        String userId=map.get("userId");
        if(com.meiqi.openservice.commons.util.StringUtils.isEmpty(userId)){
            userId="0";
        }
        String code = smsCodeAction.sendSMSCode(smsCode, Integer.parseInt(userId));
        return code;
    }*/



    // 获取文本信息的短信接口

    /*public String sendSms(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        Map<String, String> map = DataUtil.parse(repInfo.getParam(), Map.class);
        SMSCode smsCode = new SMSCode();
        String site_id=map.get("site_id");
        smsCode.setPhone(map.get("phone"));
        smsCode.setType(Byte.valueOf(map.get("codeType")));
        smsCode.setMsg(map.get("msg"));
        String code = smsCodeAction.sendSMSCode(smsCode, 0);
        
        //将发送记录写入数据库
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String serviceName="MeiqiServer_ecs_messaging_send";
        Action action = new Action();
        action.setSite_id(Integer.parseInt(site_id));
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("receive_phone", map.get("phone"));
        set.put("messaging_content", map.get("msg"));
        set.put("is_succeed", "1");
        set.put("send_time", DateUtils.getSecond());
        set.put("template_id", SysConfig.getValue("storeInfo_template_id"));
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        
        Map<String,Object> param1=new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse=null;
        String res1=mushroomAction.offer(actionReqInfo);
        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);
        if(!Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){
             LogUtil.error("CodeAction sendSms error:"+res1);
        }
        return code;
    }*/



    // 生成折扣吗
    public String buildDiscountCode(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        Map<String, String> map = DataUtil.parse(repInfo.getParam(), Map.class);
        Integer total = Integer.parseInt(map.get("total"));// 本次生成折扣码的数量
        int size = 50;
        for (int i = 0; i < total; i++) {
            if (i != 0 && ((i + 1) % size == 0)) {
                // 使用多线程做业务
                BatBuildDiscountCodeThread thread = new BatBuildDiscountCodeThread(size, map);
                indexTheadHelper.execute(thread);
            }
            if (i == total - 1) {
                // 使用多线程做业务
                int size1 = total % size;
                BatBuildDiscountCodeThread thread = new BatBuildDiscountCodeThread(size1, map);
                indexTheadHelper.execute(thread);
            }
        }
        String activity_id = map.get("activityId");
        // 获取activiy_rule的use_amount
        String DISCOUNT_BUV1_activiy = "DISCOUNT_BUV1_activiy";// 获取订单总金额，已付金额的service
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName(DISCOUNT_BUV1_activiy);
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("aid", activity_id);
        queryParam.put("limit_start", 0);
        queryParam.put("limit_end", 1);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        RuleServiceResponseData responseData = null;
        String data = dataAction.getData(serviceReqInfo, "");
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
            List<Map<String, String>> list = responseData.getRows();
            Map<String, String> resultMap = list.get(0);
            String useAmount = resultMap.get("use_amount");
            String maxAmount = resultMap.get("max_amount");

            if (StringUtils.isNotEmpty(useAmount)) {
                int old_use_amount = Integer.parseInt(useAmount);
                int max_amount = Integer.parseInt(maxAmount);
                if ((old_use_amount + total) > max_amount) {
                    return "fail";
                }
                // 向表activiy_rule的修改use_amount
                DsManageReqInfo actionReqInfo = new DsManageReqInfo();
                actionReqInfo.setServiceName("MUSH_Offer");
                String serviceName = "activiy_rule";
                Action action = new Action();
                action.setType("U");
                action.setServiceName(serviceName);
                Map<String, Object> set = new HashMap<String, Object>();
                action.setServiceName(serviceName);
                int use_amount = total + old_use_amount;
                set.put("use_amount", use_amount);
                action.setSet(set);

                Where where = new Where();
                where.setPrepend("and");
                List<SqlCondition> cons = new ArrayList<SqlCondition>();
                SqlCondition con = new SqlCondition();
                con.setKey("activity_id");
                con.setOp("=");
                con.setValue(activity_id);
                cons.add(con);
                where.setConditions(cons);
                action.setWhere(where);

                List<Action> actions = new ArrayList<Action>();
                actions.add(action);
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("actions", actions);
                param.put("transaction", 1);
                actionReqInfo.setParam(param);
                SetServiceResponseData actionResponse = null;
                String res1 = mushroomAction.offer(actionReqInfo);
                actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
                if (!Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
                    LOG.info("build discount code total:" + total);
                }
            } else {
                return "fail";
            }
        }
        return "success";
    }


    class BatBuildDiscountCodeThread implements ThreadCallback {
        private int                 size;
        private Map<String, String> map;



        public BatBuildDiscountCodeThread(int size, Map<String, String> map) {
            super();
            this.size = size;
            this.map = map;
        }



        @Override
        public void run() {
            for (int i = 0; i < size; i++) {
                buildDiscountCode(map);
            }
        }
    }



    public String buildDiscountCode(Map<String, String> map) {

        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String serviceName = "lejj_discount_info_app";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        action.setServiceName(serviceName);
        set.put("add_time", DateUtils.getSecond());
        set.put("relate_phone", map.get("relatePhone"));
        set.put("relate_order", map.get("relateOrder"));
        set.put("activity_id", map.get("activityId"));
        set.put("is_use", 1);
        // 生成一个唯一的折扣码
        String discountCode = CodeUtils.getDiscountCode();
        set.put("discount_code", discountCode);
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        SetServiceResponseData actionResponse = null;
        String res1 = mushroomAction.offer(actionReqInfo);
        actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);

        // 此处不建议使用递归，容易导致栈溢出，可以在外层进行判断
        if (!Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
            LOG.info("build discount code=" + discountCode + "重复");
            // 如果不成功继续生成
            buildDiscountCode(map);
        }
        return "success";
    }


    // 生成红包序列码
    public String buildEcsShopBonusCode(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        Map<String, String> map = DataUtil.parse(repInfo.getParam(), Map.class);
        String bonus_id = map.get("bonus_id");
        if(StringUtils.isEmpty(bonus_id) || StringUtils.isEmpty(map.get("total"))){
            return "请传入参数名bonus_id:'活动id' 或生成数量:total:总数";
        }

        //更具活动id查询可以生成数量

        // 获取activiy_rule的use_amount
        String hmj_hsv1_bonus_list = "HMJ_HSV1_bonus_list";// 获取订单总金额，已付金额的service
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName(hmj_hsv1_bonus_list);
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("bonus_id", bonus_id);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        Integer total = Integer.parseInt(map.get("total"));// 本次生成折扣码的数量

        RuleServiceResponseData responseData = null;

        String data = dataAction.getData(serviceReqInfo, "");
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
            List<Map<String, String>> list=responseData.getRows();
            Map<String, String> resultMap=list.get(0);
            String max_count=resultMap.get("issue_max_amount");
            String gened_count=resultMap.get("create_num");

            if(StringUtils.isNotEmpty(max_count)){
                int maxCount = Integer.parseInt(max_count);
                int genedCount = Integer.parseInt(gened_count);
                if((genedCount+total)>maxCount){
                    return "fail 已经达到最大数量 max_amount："+maxCount+",old_use_amount:"+genedCount+",本次要生成折扣码的数量："+total;
                }

                int size = 50;
                for (int i = 0; i < total; i++) {
                    if (i != 0 && ((i + 1) % size == 0)) {
                        // 使用多线程做业务
                        BatBuildBonusCodeThread thread = new BatBuildBonusCodeThread(size, map);
                        indexTheadHelper.execute(thread);
                    }
                    if (i == total - 1) {
                        // 使用多线程做业务
                        int size1 = total % size;
                        BatBuildBonusCodeThread thread = new BatBuildBonusCodeThread(size1, map);
                        indexTheadHelper.execute(thread);
                    }
                }

            }else{
                return "fail";
            }
        }

        return "success";
    }

    class BatBuildBonusCodeThread implements ThreadCallback {
        private int                 size;
        private Map<String, String> map;



        public BatBuildBonusCodeThread(int size, Map<String, String> map) {
            super();
            this.size = size;
            this.map = map;
        }



        @Override
        public void run() {
            for (int i = 0; i < size; i++) {
                buildEcshopEcsBonus(map);
            }
        }
    }

    public String buildEcshopEcsBonus(Map<String, String> map) {

        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String serviceName = "test_ecshop_ecs_bonus_sequence";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        action.setServiceName(serviceName);
        set.put("create_time", DateUtils.getSecond());
        set.put("bonus_id", map.get("bonus_id"));
        // 生成一个唯一码
        String seqCode = CodeUtils.getBonusCode();
        set.put("sequence_sn", seqCode);
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        SetServiceResponseData actionResponse = null;
        String res1 = mushroomAction.offer(actionReqInfo);
        actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);

        // 此处不建议使用递归，容易导致栈溢出，可以在外层进行判断
        if (!Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
            LOG.info("build Bonus code=" + seqCode + "重复");
            // 如果不成功继续生成
            buildEcshopEcsBonus(map);
        }
        return "success";
    }
}

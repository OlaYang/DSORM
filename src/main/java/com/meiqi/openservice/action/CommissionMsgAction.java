package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.DateUtil;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @ClassName: CommissionAction
 * @Description: TODO(佣金二期 脚本定时运行)
 * @author fangqi
 * @date 2015年10月12日 下午4:12:27
 *
 */
@Service
public class CommissionMsgAction extends BaseAction {

    @Autowired
    private IDataAction     dataAction;
    @Autowired
    private IMushroomAction mushroomAction;

    public void setCommission(){
        long start = System.currentTimeMillis();
        LogUtil.info("=======================================佣金二期脚本开始运行=================================================");
        LogUtil.info("commission:开始时间["+DateUtils.formatDateToString(new Date(start))+"]");
        goodsSaleCommission();
        LogUtil.info("goodsSaleCommission:结束时间["+DateUtils.formatDateToString(new Date(start))+"],耗时["+(System.currentTimeMillis()-start)+"ms]");
        start = System.currentTimeMillis();
        oneOrderCommission();
        LogUtil.info("oneOrderCommission:结束时间["+DateUtils.formatDateToString(new Date(start))+"],耗时["+(System.currentTimeMillis()-start)+"ms]");
        start = System.currentTimeMillis();
        firstOrderCommission();
        LogUtil.info("firstOrderCommission:结束时间["+DateUtils.formatDateToString(new Date(start))+"],耗时["+(System.currentTimeMillis()-start)+"ms]");
        start = System.currentTimeMillis();
        firstOrderRecommendCommission();
        LogUtil.info("firstOrderRecommendCommission:结束时间["+DateUtils.formatDateToString(new Date(start))+"],耗时["+(System.currentTimeMillis()-start)+"ms]");
        LogUtil.info("======================================佣金二期脚本开始结束==================================================");
    }

    /**
     * 
     * @Title: goodsSaleCommission
     * @Description: TODO(商品销售佣金)
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    private void goodsSaleCommission() {
        dealWithCommission("COM_HSV1_GoodsSaleCom", "1", "2");
    }



    /**
     * 
     * @Title: OneOrderCommission
     * @Description: TODO(单笔订单满额奖励)
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    private void oneOrderCommission() {
        dealWithCommission("COM_HSV1_OneOrderCom", "6", "1");
    }



    /**
     * 
     * @Title: firstOrderCommission
     * @Description: TODO(首单奖励佣金)
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    private void firstOrderCommission() {
        dealWithCommission("COM_HSV1_FirstOrderCom", "4", "3");
    }



    /**
     * 
     * @Title: shopSellCommission
     * @Description: TODO(首单推荐奖励佣金)
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    private void firstOrderRecommendCommission() {
        ApplicationContext applicationContext = MyApplicationContextUtil.getContext();
        dataAction = (IDataAction) applicationContext.getBean("dataAction");
        mushroomAction = (IMushroomAction) applicationContext.getBean("mushroomAction");
        DsManageReqInfo ruleInfo = new DsManageReqInfo();
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        reqInfo.setServiceName("COM_HSV1_FirstOrderCom");
        Map<String, Object> reqMap = new HashMap<String, Object>();
        ResponseInfo respInfo = DataUtil.parse(dataAction.getData(reqInfo), ResponseInfo.class);
        List<Map<String, String>> list = respInfo.getRows();
        if (null == list || list.size() == 0) {
            LogUtil.info("firstOrderRecommendCommission:规则[COM_HSV1_FirstOrderCom]获取信息为空！");
            return;
        }
        List<DsManageReqInfo> reqList = new ArrayList<DsManageReqInfo>();
        Map<String, Object> set = null;
        Map<String, Object> param1 = null;
        List<Action> actions = null;
        Map<String, String> bankResult = null;
        Map<String, String> formResult = null;
        for (Map<String, String> map : list) {
            String commission_sn = "";
            if ((map.containsKey("com_money_TJ") && (null != map.get("com_money_TJ") && !"".equals(map
                    .get("com_money_TJ"))))
                    && (map.containsKey("status_TJ") && map.get("status_TJ").equals("1"))
                    && (null != map.get("send_user_id") && !"".equals(map.get("send_user_id")))) {
                set = new HashMap<String, Object>();
                long num = 0;
                while (num < 10000) {
                    num = Math.round(Math.random() * 100000);
                }
                commission_sn = "JS" + DateUtils.timeToDate(System.currentTimeMillis(), "yyyyMMdd") + num;
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_lejj_commission_info");
                set.put("commission_sn", commission_sn);// 结算单编号
                set.put("settlement_amount", map.get("com_money_TJ"));// 佣金金额
                set.put("user_id", map.get("send_user_id"));
                set.put("frm_id", map.get("frm_id_TJ"));// 表单ID
                set.put("user_type", 2);// 用户类型
                set.put("settlement_status", 0);// 结算状态
                set.put("commission_type", 5);// 提佣类型
                set.put("settlement_time", System.currentTimeMillis() / 1000);// 结算日期
                                                                              // 当前时间戳
                set.put("create_time", System.currentTimeMillis() / 1000);// 创建日期
                                                                          // 当前时间戳
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                param1.put("transaction", 1);
                reqInfo.setParam(param1);
                String res1 = mushroomAction.offer(reqInfo);
                respInfo = DataUtil.parse(res1, ResponseInfo.class);
                if (respInfo.getCode().equals("0")) {
                    ruleInfo.setServiceName("COM_HSV1_SettleList");
                    reqMap.put("bill_sn", commission_sn);
                    ruleInfo.setParam(reqMap);
                    respInfo = DataUtil.parse(dataAction.getData(ruleInfo), ResponseInfo.class);
                    list = respInfo.getRows();
                    if (null != list && list.size() > 0) {
                        bankResult = new HashMap<String, String>();
                        bankResult = list.get(0);
                    }
                }
            }

            if (null == bankResult) {
                LogUtil.info("firstOrderRecommendCommission:没有结算单编号，相关信息为空！");
            }

            if ((map.containsKey("com_money_TJ") && (null != map.get("com_money_TJ") && !"".equals(map.get("com_money_TJ"))))
                    && (map.containsKey("status_TJ") && map.get("status_TJ").equals("1"))
                    && (null != map.get("send_user_id") && !"".equals(map.get("send_user_id")))) {
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_ecs_commission_order");
                set = new HashMap<String, Object>();
                set.put("order_id", map.get("order_id"));
                set.put("user_id", map.get("send_user_id"));
                set.put("role_id", map.get("role_id"));// 角色ID
                set.put("add_time", System.currentTimeMillis() / 1000);
                set.put("commission_type", 4);// 提佣类型
                set.put("commission_id", null != bankResult ? bankResult.get("commission_id") : "");
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                reqInfo.setParam(param1);
                reqList.add(reqInfo);
            }

            if (map.containsKey("com_money_TJ")
                    && (null != map.get("com_money_TJ") && !"".equals(map.get("com_money_TJ")))) {
                set = new HashMap<String, Object>();
                set.put("frm_id", map.get("frm_id_TJ"));
                ruleInfo.setServiceName("COM_BUV1_auditinfo");
                reqMap.put("frm_id", map.get("frm_id_TJ"));
                ruleInfo.setParam(reqMap);
                respInfo = DataUtil.parse(dataAction.getData(ruleInfo), ResponseInfo.class);
                list = respInfo.getRows();
                if (null != list && list.size() > 0) {
                    formResult = new HashMap<String, String>();
                    formResult = list.get(0);
                }
                if (null == formResult) {
                    LogUtil.info("firstOrderRecommendCommission:表单编号[" + map.get("frm_id_TJ") + "]对应消息为空！");
                }
                if (null != formResult && formResult.get("is_audit").equals("0")) {
                    set.put("audit_type", 3);
                }
                if (null != formResult && formResult.get("is_audit").equals("1")) {
                    set.put("audit_type", 0);
                }
                set.put("commission_id", bankResult.get("commission_id"));
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_ecs_audit_detail");
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                reqInfo.setParam(param1);
                reqList.add(reqInfo);
            }

            if (null != formResult && formResult.get("is_audit").equals("0")) {
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_lejj_batch_pay");
                set = new HashMap<String, Object>();
                set.put("bill_type", 1);
                set.put("bill_sn", commission_sn);
                set.put("transfer_status", 0);
                set.put("amount", map.get("com_money_TJ"));
                set.put("bank", bankResult.get("bank_name"));
                set.put("account", bankResult.get("account_number"));
                set.put("unionpay_no", bankResult.get("unionpay_no"));
                set.put("creat_time", System.currentTimeMillis());
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                reqInfo.setParam(param1);
                reqList.add(reqInfo);
            }

        }
        // TODO 遍历list 并写入
        commissionInsert2DB(reqList, "firstOrderRecommendCommission:COM_HSV1_FirstOrderCom");
    }



    /**
     * 
     * @Title: dealWithCommission
     * @Description: TODO(处理佣金信息)
     * @param @param serviceName 参数说明
     * @return void 返回类型
     * @throws
     */
    private void dealWithCommission(String serviceName, String commission_type1, String commission_type2) {
        ApplicationContext applicationContext = MyApplicationContextUtil.getContext();
        dataAction = (IDataAction) applicationContext.getBean("dataAction");
        mushroomAction = (IMushroomAction) applicationContext.getBean("mushroomAction");
        DsManageReqInfo ruleInfo = new DsManageReqInfo();
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        reqInfo.setServiceName(serviceName);
        Map<String, Object> reqMap = new HashMap<String, Object>();
        ResponseInfo respInfo = DataUtil.parse(dataAction.getData(reqInfo), ResponseInfo.class);
        List<Map<String, String>> list = respInfo.getRows();
        if (null == list || list.size() == 0) {
            LogUtil.info("dealWithCommission:规则[" + serviceName + "]获取信息为空！");
            return;
        }
        List<DsManageReqInfo> reqList = new ArrayList<DsManageReqInfo>();
        Map<String, Object> set = null;
        Map<String, Object> param1 = null;
        List<Action> actions = null;
        Map<String, String> bankResult = null;
        Map<String, String> formResult = null;
        for (Map<String, String> map : list) {
            String commission_sn = "";
            if ((map.containsKey("com_money") && (null != map.get("com_money") && !"".equals(map.get("com_money"))))
                    && (map.containsKey("status") && map.get("status").equals("1"))
                    && (null != map.get("user_id") && !map.get("user_id").equals(""))) {
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                set = new HashMap<String, Object>();
                long num = 0;
                while (num < 10000) {
                    num = Math.round(Math.random() * 100000);
                }
                commission_sn = "JS" + DateUtils.timeToDate(System.currentTimeMillis(), "yyyyMMdd") + num;
                action.setServiceName("test_ecshop_lejj_commission_info");
                set.put("commission_sn", commission_sn);// 结算单编号
                set.put("settlement_amount", map.get("com_money"));// 佣金金额
                set.put("user_id", map.get("user_id"));
                set.put("frm_id", map.get("frm_id"));// 表单ID
                set.put("user_type", 2);// 用户类型
                set.put("settlement_status", 0);// 结算状态
                set.put("commission_type", commission_type1);// 提佣类型
                set.put("settlement_time", System.currentTimeMillis() / 1000);// 结算日期
                                                                              // 当前时间戳
                set.put("create_time", System.currentTimeMillis() / 1000);// 创建日期
                                                                          // 当前时间戳
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                param1.put("transaction", 1);
                reqInfo.setParam(param1);
                // reqList.add(reqInfo);
                String res1 = mushroomAction.offer(reqInfo);
                respInfo = DataUtil.parse(res1, ResponseInfo.class);
                if (respInfo.getCode().equals("0")) {
                    ruleInfo = new DsManageReqInfo();
                    ruleInfo.setServiceName("COM_HSV1_SettleList");
                    reqMap.put("bill_sn", commission_sn);
                    ruleInfo.setParam(reqMap);
                    respInfo = DataUtil.parse(dataAction.getData(ruleInfo), ResponseInfo.class);
                    list = respInfo.getRows();
                    if (null != list && list.size() > 0) {
                        bankResult = new HashMap<String, String>();
                        bankResult = list.get(0);
                    }
                }
            }

            if (null == bankResult) {
                LogUtil.info("dealWithCommission:没有结算单编号，相关信息为空！");
            }

            if ((map.containsKey("com_money") && (null != map.get("com_money") && !"".equals(map.get("com_money"))))
                    && (map.containsKey("status") && map.get("status").equals("1"))
                    && (null != map.get("user_id") && !map.get("user_id").equals(""))) {
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_ecs_commission_order");
                set = new HashMap<String, Object>();
                set.put("order_id", map.get("order_id"));
                set.put("user_id", map.get("user_id"));
                set.put("role_id", map.get("role_id"));// 表单ID
                set.put("add_time", System.currentTimeMillis() / 1000);
                set.put("commission_type", commission_type2);// 提佣类型
                set.put("commission_id", null != bankResult ? bankResult.get("commission_id") : "");
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                reqInfo.setParam(param1);
                reqList.add(reqInfo);
            }

            if (map.containsKey("com_money") && (null != map.get("com_money") && !"".equals(map.get("com_money")))) {
                ruleInfo = new DsManageReqInfo();
                ruleInfo.setServiceName("COM_BUV1_auditinfo");
                reqMap.put("frm_id", map.get("frm_id"));
                ruleInfo.setParam(reqMap);
                respInfo = DataUtil.parse(dataAction.getData(ruleInfo), ResponseInfo.class);
                list = respInfo.getRows();
                if (null != list && list.size() > 0) {
                    formResult = new HashMap<String, String>();
                    formResult = list.get(0);
                }

                if (null == formResult) {
                    LogUtil.info("goodsSaleCommission:表单编号[" + map.get("frm_id") + "]对应消息为空！");
                }
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_ecs_audit_detail");
                set = new HashMap<String, Object>();
                set.put("frm_id", map.get("frm_id"));
                if (null != formResult && formResult.get("is_audit").equals("0")) {
                    set.put("audit_type", 3);
                }
                if (null != formResult && formResult.get("is_audit").equals("1")) {
                    set.put("audit_type", 0);
                }
                set.put("commission_id", bankResult.get("commission_id"));
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                reqInfo.setParam(param1);
                reqList.add(reqInfo);
            }

            if (null != formResult && formResult.get("is_audit").equals("0")) {
                reqInfo = new DsManageReqInfo();
                reqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setType("C");
                action.setServiceName("test_ecshop_lejj_batch_pay");
                set = new HashMap<String, Object>();
                set.put("bill_type", 1);
                set.put("bill_sn", commission_sn);
                set.put("transfer_status", 0);
                set.put("amount", map.get("com_money"));
                set.put("bank", bankResult.get("bank_name"));
                set.put("account", bankResult.get("account_number"));
                set.put("unionpay_no", bankResult.get("unionpay_no"));
                set.put("creat_time", System.currentTimeMillis() / 1000);
                action.setSet(set);
                actions = new ArrayList<Action>();
                actions.add(action);
                param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                reqInfo.setParam(param1);
                reqList.add(reqInfo);
            }

        }
        // TODO 遍历list 并写入
        commissionInsert2DB(reqList, "dealWithCommission:" + serviceName);
    }



    /**
     * 
     * @Title: commissionInsert2DB
     * @Description: TODO(佣金信息入库)
     * @param @param list
     * @param @param str 规则名称
     * @return void 返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    private void commissionInsert2DB(List<DsManageReqInfo> list, String str) {
        if (null == list || list.size() == 0) {
            LogUtil.info("commissionInsert2DB_info:[" + str + "]获取佣金信息为空！");
            return;
        }
        long start = System.currentTimeMillis();
        LogUtil.info("commissionInsert2DB_start:[" + str + "]入库开始,入库数量["+list.size()+"],开始时间["+DateUtils.formatDateToString(new Date(start))+"]");
        for (int i=0;i<list.size();i++) {
            String res1 = mushroomAction.offer(list.get(i));
            ResponseInfo respInfo = DataUtil.parse(res1, ResponseInfo.class);
            if (respInfo.getCode().equals("1")) {
                LogUtil.info("commissionInsert2DB_error:[" + str + "],写入异常索引["+i+"],写入参数["+DataUtil.toJSONString(list.get(i))+"]" + respInfo.getDescription());
            }else{
                DsManageReqInfo ds = list.get(i);
                Map<String, Object> param = ds.getParam();
                List<Action> actions = (List<Action>) param.get("actions");
                Action action = actions.get(0);
                Map<String, Object> map = action.getSet();
                if(null!=map&&map.containsKey("bill_sn")){//是否有结算单编号
                    commissionLogInsert2DB(map.get("bill_sn").toString());//生成结算单日志
                }
            }
        }
        long end = System.currentTimeMillis();
        LogUtil.info("commissionInsert2DB_end:[" + str + "]入库结束,入库数量["+list.size()+"],结束时间["+DateUtils.formatDateToString(new Date(end))+"],耗时["+(end-start)+"]");
    }
    
    /**
     * 
    * @Title: commissionLogInsert2DB 
    * @Description: TODO(生成结算单日志) 
    * @param   参数说明 
    * @return void    返回类型 
    * @throws
     */
    private void commissionLogInsert2DB(String bill_sn){
        DsManageReqInfo ds = new DsManageReqInfo();
        ds.setServiceName("COM_BUV1_Settlement");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("bill_sn", bill_sn);
        ds.setParam(map);
        ResponseInfo res = DataUtil.parse(dataAction.getData(ds), ResponseInfo.class);
        List<Map<String,String>> list = res.getRows();
        if(null!=list&&list.size()>0){
            Map<String,String> resMap = list.get(0);
            ds = new DsManageReqInfo();
            ds.setServiceName("MUSH_Offer");
            Action action = new Action();
            action.setType("C");
            action.setServiceName("test_ecshop_ecs_action_log");
            if(null==resMap||!resMap.containsKey("commission_id")){
                LogUtil.info("commissionLogInsert2DB:[COM_BUV1_Settlement]规则,参数["+DataUtil.toJSONString(map)+"],没有返回结算单ID!");
                return ;
            }
            map.clear();
            map.put("bill_id",resMap.get("commission_id"));
            map.put("action_user", "admin");
            map.put("action_time", DateUtil.getSecond());
            map.put("action_content", "生成结算单");
            map.put("type", 16);
            action.setSet(map);
            List<Action> actions = new ArrayList<Action>();
            actions.add(action);
            Map<String, Object> param1 = new HashMap<String, Object>();
            param1.put("actions", actions);
            ds.setParam(param1);
            String res1 = mushroomAction.offer(ds);
            ResponseInfo respInfo = DataUtil.parse(res1, ResponseInfo.class);
            if (respInfo.getCode().equals("1")) {
                LogUtil.info("commissionLogInsert2DB_error:写入参数["+DataUtil.toJSONString(res1)+"]" + respInfo.getDescription());
            }
        }
    }
}

package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.Arith;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.ListUtil;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.PayService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.commons.util.CollectionsUtils;
import com.meiqi.openservice.commons.util.StringUtils;

@Service
public class PayServiceImpl implements PayService {
	
    private String up="up";//支付传给第三方的分隔符
	@Autowired
    private EtagService eTagService;
	private static final Log LOG =  LogFactory.getLog("pay");
    //private ConcurrentHashMap<String,String> map=new ConcurrentHashMap<String, String>(10000);//支付內存日誌
    @Autowired
    private IDataAction dataAction;
    @Autowired
    private IMushroomAction mushroomAction;
    @Override
    public Map<String,Object> paySuccess(String out_trade_no, String trade_no, double total_fee, String trade_status,Integer payType,String getherAccount,String paidAccount,String paid_card,String remark) {

            int currentTime=DateUtils.getSecond();
            Map<String,Object> operateResult=new HashMap<String, Object>();
	        String[] array=out_trade_no.split(up);
	        String ourOrderSn=array[0];//自己系统内部的订单编号
	        String moneyType="2";//支付金额类型
	        if(array.length>=3){
	            if(StringUtils.isNotEmpty(array[2])){
	                moneyType=array[2];
	            }
	        }
    		String serviceName_esc_pay_log = "COM_HSV1_pay";// 获取订单总金额，已付金额的service
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName(serviceName_esc_pay_log);
            Map<String,Object> param=new HashMap<String, Object>();
            param.put("ordersn", ourOrderSn);
            param.put("type", moneyType);//支付金额类型
            serviceReqInfo.setParam(param);
            serviceReqInfo.setNeedAll("1");
            // 通过rule获取ecs_pay_log 的支付总金额
            // 通过rule 去拿到此订单对应的已付金额，for update 操作
            RuleServiceResponseData responseData = null;
            //String data = HttpUtil.post(getUrl, content);
            String data =dataAction.getData(serviceReqInfo,"");
            responseData = DataUtil.parse(data, RuleServiceResponseData.class);
            if(ListUtil.notEmpty(responseData.getRows())){
                	responseData.setCode(Constants.GetResponseCode.SUCCESS);
            }
            if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                // 拿到金额
            	List<Map<String, String>> list=responseData.getRows();
            	Map<String, String> map=list.get(0);
                double orderMoney = Double.valueOf(map.get("order_amount").toString());// 订单总金额
                double paidMoney = Double.valueOf(map.get("paid").toString());;// 订单已付金额
                //String userId=map.get("user_id").toString();
    	        String orderId=map.get("order_id").toString();
    	        String previousOrderStatus=map.get("order_status").toString();
    	        String userName="";
    	        if(map.get("user_name")!=null){
    	            userName=map.get("user_name").toString();
    	        }
                //待付款
                if(paidMoney<orderMoney){
                    //根据订单号，交易号，支付类型判断ecs_pay_log中是否已经存在,线下转账不做校验，因为没有交易号
                    if(Constants.PAY_TYPE.OFFINE_PAYMENT!=payType){
                        DsManageReqInfo serviceReqInfoTmp=new DsManageReqInfo();
                        serviceReqInfoTmp.setServiceName("YJG_BUV1_order_pay");
                        Map<String,Object> paramTmp=new HashMap<String, Object>();
                        if(Constants.PAY_TYPE.POS_PAYMENT!=payType){
                            //如果是第三方支付的，需要加上支付方式作为判断条件
                            paramTmp.put("pay_type", payType);
                        }
                        paramTmp.put("audit_results", "0,1");//审核结果：0 待审核  1通过  2 拒绝
                        paramTmp.put("transaction_id", trade_no);
                        serviceReqInfoTmp.setParam(paramTmp);
                        serviceReqInfoTmp.setNeedAll("1");
                        RuleServiceResponseData responseDataTmp = null;
                        String dataTmp =dataAction.getData(serviceReqInfoTmp,"");
                        responseDataTmp = DataUtil.parse(dataTmp, RuleServiceResponseData.class);
                        List<Map<String,String>> listTmp=responseDataTmp.getRows();
                        if(!CollectionsUtils.isNull(listTmp)){
                                if(Constants.PAY_TYPE.POS_PAYMENT!=payType){
                                    String error="支付记录已经存在,req:"+"out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"+ trade_status + ",total_fee:" + total_fee+",payType:"+payType;
                                    LOG.info(error);
                                    operateResult.put("payResult", "success");
                                    operateResult.put("errorMsg", error);
                                    return operateResult;
                                }else{
                                    String error="支付记录已经存在,req:"+"out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"+ trade_status + ",total_fee:" + total_fee+",payType:"+payType;
                                    LOG.info(error);
                                    operateResult.put("payResult", "fail");
                                    operateResult.put("errorMsg", "支付记录已经存在");
                                    return operateResult;
                                }
                        }
                    }
	                String serviceName = "ecs_pay_log_app";
	            	String serviceName1 = "ecs_order_info_app";
	            	String serviceName2 = "test_ecshop_ecs_order_action";
	            	
	                // 调用mushroom 向ecs_pay_log插入支付流水记录
	            	DsManageReqInfo actionReqInfo = new DsManageReqInfo();
	            	actionReqInfo.setServiceName("MUSH_Offer");
	    	        
	    	        Action action = new Action();
	    	        action.setType("C");
	    	        action.setServiceName(serviceName);
	    	        Map<String, Object> set = new HashMap<String, Object>();
	    	        set.put("order_id", orderId);
	    	        set.put("order_amount", total_fee);
	    	        //set.put("order_type", Constants.ORDER_TYPE.NORMAL);//此字段不维护了
	    	        set.put("is_paid", Constants.PAY_STATUS.PAID);
	    	        set.put("pay_type",  payType);
	    	        set.put("add_time", currentTime);
	    	        set.put("transaction_id", trade_no);
	    	        set.put("third_order_sn", out_trade_no);
	    	        set.put("gether_account", getherAccount);
	    	        set.put("paid_account", paidAccount);
	    	        set.put("paid_card", paid_card);//付款卡号
                    set.put("remark", remark);//备注
	    	        set.put("creat_name", userName);
	    	        if(Constants.PAY_TYPE.ALIPAY==payType){
	    	            //功能 #9459   前台支付宝付款建立收款单时，收款单状态调整为审核通过且到账。
	    	            set.put("audit_results", 1);//审核通过
	                    set.put("is_account", 1);//已经到账
	    	        }
	    	        action.setSet(set);
	
	    	        
	    	        Action action1 = new Action();
		            action1.setType("U");
		            action1.setServiceName(serviceName1);
		            Map<String, Object> set1 = new HashMap<String, Object>();
		            double allPaidMoney = Arith.add(paidMoney, total_fee);
		            if(Constants.PAY_TYPE.ALIPAY==payType){
		                //需求功能 #10303订单已收款金额在收款单为审核通过时进行维护增加
		                //当付款类型为支付宝且状态为审核通过，才更新ecs_order_info的money_paid字段，（原值+此次付款通过的金额）为现有的money_paid
		                //因为只有支付宝的audit_results 状态值为审核通过
		                set1.put("money_paid", allPaidMoney);
                    }
		            if (orderMoney == allPaidMoney) {
		                     set1.put("pay_status", Constants.PAY_STATUS.PAID);
		            } else {
		                     set1.put("pay_status", Constants.PAY_STATUS.PART_PAID);
		            }
		            set1.put("order_status", Constants.ORDER_STATUS.CONFIRMED);
		            set1.put("pay_time", currentTime);
		            action1.setSet(set1);
	
		            Where where1 = new Where();
		            where1.setPrepend("and");
	
		            List<SqlCondition> cons1 = new ArrayList<SqlCondition>();
		            SqlCondition con1 = new SqlCondition();
		            con1.setKey("order_id");
		            con1.setOp("=");
		            con1.setValue(orderId);
		            cons1.add(con1);
	
		            where1.setConditions(cons1);
		            action1.setWhere(where1);
	    	        
		            Action action2 = new Action();
		            action2.setType("C");
		            action2.setServiceName(serviceName2);
                    Map<String, Object> set2 = new HashMap<String, Object>();
                    set2.put("order_id", orderId);
                    if(StringUtils.isNotEmpty(userName)){
                      set2.put("action_user",userName);
                    }
                    if (orderMoney == allPaidMoney) {
                        set2.put("order_status",1);
                        set2.put("shipping_status",0);
                        set2.put("pay_status",2);
                    }else{
                        set2.put("order_status",1);
                        set2.put("shipping_status",0);
                        set2.put("pay_status",5);
                    }
                    set2.put("action_note","您已付款【"+total_fee+"】元。");
                    set2.put("log_time",currentTime);
                    set2.put("is_show","1");
                    action2.setSet(set2);
                    
		            
	    	        List<Action> actions = new ArrayList<Action>();
	    	        actions.add(action);
	    	        actions.add(action1);
	    	        actions.add(action2);
	    	        
	    	        Map<String,Object> param1=new HashMap<String, Object>();
	    	        param1.put("actions", actions);
	    	        param1.put("transaction", 1);
	    	        actionReqInfo.setParam(param1);
	    	        SetServiceResponseData actionResponse=null;
	    	        String res1=mushroomAction.offer(actionReqInfo);
	    	        LOG.info("pay mushroom result="+res1+",req:"+"out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"+ trade_status + ",total_fee:" + total_fee+",payType:"+payType);
	    	        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);
	    	        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){
	    	            	eTagService.putEtagMarking("order/"+orderId,Long.toString(System.currentTimeMillis()));
	    	            	operateResult.put("payResult", "success");
	    	            	operateResult.put("previousOrderStatus", previousOrderStatus);
	    	            	operateResult.put("order_sn", ourOrderSn);
	    	            	operateResult.put("order_status", Constants.ORDER_STATUS.CONFIRMED);
	                        return operateResult;
	    	        }
                }else{
                    //已经全部付款
                    LOG.info("已经全部付款,req:"+"out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"+ trade_status + ",total_fee:" + total_fee+",payType:"+payType);
                    operateResult.put("errorMsg", "此订单支付状态为已付款");
                    if(Constants.PAY_TYPE.OFFINE_PAYMENT!=payType && Constants.PAY_TYPE.POS_PAYMENT!=payType){
                        operateResult.put("payResult", "success");
                    }else{
                        operateResult.put("payResult", "fail");
                    }
                    return operateResult;
                }
            }
        operateResult.put("payResult", "fail");
        return operateResult;
    }

}

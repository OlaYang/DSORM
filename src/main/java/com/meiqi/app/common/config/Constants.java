package com.meiqi.app.common.config;

import java.util.HashMap;
import java.util.Map;

public interface Constants {

	//支付方式
	interface PAY_TYPE{
	  int ALIPAY=1;//支付宝支付
	  int BILLPAY=2;//快钱支付
	  int UPMPPAY=3;//银联支付
	  int OFFINE_PAYMENT=8;//线下转账付款
	  int POS_PAYMENT=7;//线下POS刷卡
	  int WECHAT_PAY=6;//微信支付
	  int KUAIQIAN_OQS=10;//快钱pos机支付
	}
	@SuppressWarnings("serial")
	Map<Integer, String> PAY_TYPE_NAME_MAP = new HashMap<Integer, String>() {
			{
				put(PAY_TYPE.ALIPAY, "支付宝支付");
				put(PAY_TYPE.BILLPAY, "快钱支付");
				put(PAY_TYPE.UPMPPAY, "银联支付");
				put(PAY_TYPE.KUAIQIAN_OQS, "快钱pos机支付");
			}
	};
	interface PAY_STATUS{
		int NOT_PAID=0;//未付款
		int PAYING=1;//付款中
		int PAID=2;//已付款
		int REPLAY_REFUND=3;//客户申请退款
		int REFUND_SUCCESS=4;//退款成功
		int PART_PAID=5;//部分付款
	}
	interface ORDER_STATUS{
		int UN_CONFIRM=0;//未确认
		int CONFIRMED=1;//已确认
		int CANCELED=2;//已取消
		int INVALID=3;// 无效
		int RETURN_GOODS=4;//退货
		int REALISE_DEALING=5;// 售后处理中
	}
	
	//订单类型
	interface ORDER_TYPE{
		  int NORMAL=1;//普通 订单
		  int MEMBERS_ADVANCE_PAYMENT=2;//内购 
	}
	@SuppressWarnings("serial")
	Map<Integer, String> ORDER_TYPE_NAME_MAP = new HashMap<Integer, String>() {
			{
				put(ORDER_TYPE.NORMAL, "普通 订单");
				put(ORDER_TYPE.MEMBERS_ADVANCE_PAYMENT, "内购 ");
			}
	};
	
	//DSM get返回的数据code标示
	interface GetResponseCode{
		 String NOT_SET_DATASOURCE="1";//数据源未配置
		 String NOT_SET_STYLE="1";//该数据源未设置样式
		 String NO_DATA="1";//无数据
		 String STYLE_CONVERT_ERROR="1";//样式转换错误！
		 String SUCCESS="0";//成功
		 String ERROR="1";//失败
	}
	
	@SuppressWarnings("serial")
	Map<String, String> ResponseCode_name = new HashMap<String, String>() {
			{
				put(GetResponseCode.NOT_SET_DATASOURCE, "数据源未配置");
				put(GetResponseCode.NOT_SET_STYLE, "该数据源未设置样式");
				put(GetResponseCode.NO_DATA, "无数据");
				put(GetResponseCode.STYLE_CONVERT_ERROR, "样式转换错误！");
				put(GetResponseCode.SUCCESS, "成功");
				put(GetResponseCode.ERROR, "失败");
			}
	};
	//DSM set返回的数据code标示
	interface SetResponseCode{
			 String SUCCESS="0";//成功
			 String ERROR="-1";//系统错误
	}
	@SuppressWarnings("serial")
	Map<String, String> SetResponseCode_name = new HashMap<String, String>() {
				{
					put(SetResponseCode.SUCCESS, "成功");
					put(SetResponseCode.ERROR, "系统错误");
				}
	};
	public static final boolean UPMP_TEST = true;
	
	//支付方式
	interface DIRTY_OBJECT{
		  String NOT_DIRTY="1";//非脏数据
		  String DIRTY="2";//脏数据
	}
}

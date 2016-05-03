package com.meiqi.app.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface PayService {

	/**
	 * 
	 * @param out_trade_no 订单号  此值的格式：订单编号up时间戳up订单类型 
	 * @param trade_no 交易号
	 * @param total_fee 支付金额
	 * @param trade_status 支付状态
	 * @param payType 支付方式
	 * @param getherAccount 收款账户名
	 * @param paidAccount 付款账户名
	 * @param paid_card 付款卡号
	 * @param remark 备注
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Map<String,Object>  paySuccess(String out_trade_no,String trade_no,double total_fee,String trade_status,Integer payType,String getherAccount,String paidAccount,String paid_card,String remark) throws IllegalAccessException, InvocationTargetException;
}

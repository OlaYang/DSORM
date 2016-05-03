package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @Description:佣金明细项用到的实体
 * 对应规则：APP_HSV1_CommissionInfo
 *     传入：user_id,create_time_start,create_time_end（必传）、status（可选）
 *     传出：time_1（ ）、time_2（ ）、settlement_amount（结算金额）、
 *     total_settlement_amount（总佣金）、order_sn（订单号）、
 *     order_amount（订单金额）、settlementTime（结算日期）、settlementStatus（结算状态）status
 * 
 * @author:luzicong
 * 
 * @time:2015年7月3日 下午4:30:51
 */
public class CommissionInfo {
	@ColumnKey(value = "settlementStatus")
	private String status; // 结算状态  所有2  未结算 0  已结算1
	
	@ColumnKey(value = "time_1")
	private String month; // 规则引擎在必要时会加上Year前缀
	
	@ColumnKey(value = "time_2")
	private String date;
	
	@ColumnKey(value = "settlement_amount")
	private String price; // 结算金额 佣金金额
	
	@ColumnKey(value = "order_sn")
	private String orderSn; // 订单号
	
    @ColumnKey(value = "order_amount")
	private String orderAmount; // 订单金额
	
    @ColumnKey(value = "settlementTime")
	private String setteDate; // 结算日期
	
    @ColumnKey(value = "total_settlement_amount")
	private String totalPrice; // 总佣金
    
    @ColumnKey(value = "createTime")
    private String createTime; //订单生成日期
    
    @ColumnKey(value = "account")
    private String account; //账户
    
    @ColumnKey(value = "serial_number")
    private String seriaNumber; //交易号
    
    @ColumnKey(value = "finish_time")
    private String finishTime; //打款时间
    
    @ColumnKey(value = "commission_id")
    private String commissionId;
    
    public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSeriaNumber() {
		return seriaNumber;
	}

	public void setSeriaNumber(String seriaNumber) {
		this.seriaNumber = seriaNumber;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}


	
    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getSetteDate() {
		return setteDate;
	}

	public void setSetteDate(String setteDate) {
		this.setteDate = setteDate;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	

	@Override
	public String toString() {
		return "CommissionInfo [status=" + status + ", month=" + month
				+ ", date=" + date + ", price=" + price + ", orderSn="
				+ orderSn + ", orderAmount=" + orderAmount + ", setteDate="
				+ setteDate + ", totalPrice=" + totalPrice
				+ "]";
	}
}

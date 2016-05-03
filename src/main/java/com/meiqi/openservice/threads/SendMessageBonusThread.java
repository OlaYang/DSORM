package com.meiqi.openservice.threads;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import com.meiqi.openservice.action.PayGiveRedPacketAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 发送红包短信
 * @author meiqidr
 *
 */
/**功能#12454 前台付款成功后，发送短信（调用买赠红包接口）
 * 支付金额=订单金额，异步发送短信通知
 * @author Administrator
 *
 */
public class SendMessageBonusThread implements Runnable{

	
	private String out_trade_no;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private PayGiveRedPacketAction payGiveRedPacketAction;
	
	public SendMessageBonusThread(HttpServletRequest request,HttpServletResponse response,String out_trade_no){
		super();    
		this.out_trade_no = out_trade_no;
		this.request = request;
		this.response = response;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		RepInfo  repInfo = new RepInfo();
        repInfo.setAction("payGiveRedPacketAction");
	    repInfo.setMethod("payGiveRedPacket");
	    String smsParam = "{\"order_sn\":\""+out_trade_no+"\"}";
	    repInfo.setParam(smsParam);
	    
	    if(null==payGiveRedPacketAction){
	    	ApplicationContext applicationContext=MyApplicationContextUtil.getContext();
		    payGiveRedPacketAction=(PayGiveRedPacketAction)applicationContext.getBean("payGiveRedPacketAction");
	    }
	    
	    payGiveRedPacketAction.payGiveRedPacket(request, response, repInfo);
	}

}

/**
 * 
 */
package com.meiqi.liduoo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.liduoo.base.services.ILiduooDataService;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.fastweixin.util.HttpBaseKit;
import com.meiqi.liduoo.fastweixin.util.PaymentKit;
import com.meiqi.openservice.action.pay.PayService;
import com.meiqi.util.LogUtil;

/**
 * @author FrankGui 2016年1月7日
 */
@RestController
// @Controller
@RequestMapping(value = "/wxpaycb/backnotify")
public class WeixinPayCallbackController {
	@Autowired
	ILiduooDataService dataService;

	private static LRUMap<String, String> paying = new LRUMap<String, String>(10000);

	@RequestMapping(method = RequestMethod.POST)
	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 支付结果通用通知文档:
		// https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
		String xmlMsg = HttpBaseKit.readData(request);
		System.out.println("支付通知=" + xmlMsg);
		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);

		// String result_code = params.get("result_code");
		// // 总金额
		// String totalFee = params.get("total_fee");
		// // 商户订单号
		// String orderId = params.get("out_trade_no");
		// // 微信支付订单号
		String transId = params.get("transaction_id");
		// // 支付完成时间，格式为yyyyMMddHHmmss
		// String timeEnd = params.get("time_end");
		String key = "wechat" + transId;
		if (null == PayService.paying.get(key)) {
			// 立刻加入支付业务处理队列中，防止再次通知造成业务处理重复
			PayService.paying.put(key, transId);
			String attach = params.get("attach");
			String rulePrefix = LdConfigUtil.getConfig("wechat_pay_callback_rule_prefix");
			String ruleName = rulePrefix + attach;

			// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
			// 避免已经成功、关闭、退款的订单被再次更新
			LogUtil.info("支付通知处理：调用规则：" + ruleName);
			ServiceReqInfo serviceInfo = new ServiceReqInfo();
			serviceInfo.setServiceName(ruleName);
			Map<String, Object> param = new HashMap<String, Object>();
			param.putAll(params);
			serviceInfo.setParam(param);
			try {
				Map<String, Object> result = dataService.getOneRow(serviceInfo);
				LogUtil.info("支付通知处理结果：" + result);
				response.getWriter().write("success");
			} catch (Exception ex) {
				ex.printStackTrace();
				LogUtil.error(ex.getMessage(), ex);
			}
		} else {
			// do nothing
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.doPost(request, response);

	}
}

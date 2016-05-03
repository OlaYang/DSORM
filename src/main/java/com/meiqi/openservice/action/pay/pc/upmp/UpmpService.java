package com.meiqi.openservice.action.pay.pc.upmp;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 类名：接口处理核心类 功能：组转报文请求，发送报文，解析应答报文 版本：1.0 日期：2012-10-11 作者：中国银联UPMP团队 版权：中国银联
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己的需要，按照技术文档编写,并非一定要使用该代码。该代码仅供参考。
 */
public class UpmpService {
	private static final Logger LOG = LoggerFactory.getLogger("run");

//	/**
//	 * 异步通知消息验证
//	 * 
//	 * @param para
//	 *            异步通知消息
//	 * @return 验证结果
//	 */
//	public static boolean verifySignature(Map<String, String> para) {
//		String respSignature = para.get("signature");
//		// 除去数组中的空值和签名参数
//		Map<String, String> filteredReq = UpmpCore.paraFilter(para);
//		String signature = UpmpCore.buildSignature(filteredReq);
//		if (null != respSignature && respSignature.equals(signature)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
}

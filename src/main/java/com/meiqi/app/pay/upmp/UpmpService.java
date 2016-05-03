package com.meiqi.app.pay.upmp;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 类名：接口处理核心类 功能：组转报文请求，发送报文，解析应答报文 版本：1.0 日期：2012-10-11 作者：中国银联UPMP团队 版权：中国银联
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己的需要，按照技术文档编写,并非一定要使用该代码。该代码仅供参考。
 */
public class UpmpService {
	private static final Logger LOG = LoggerFactory.getLogger("run");

	/**
	 * 交易接口处理
	 * 
	 * @param req
	 *            请求要素
	 * @param resp
	 *            应答要素
	 * @return 是否成功
	 */
	public static boolean trade(Map<String, String> req, Map<String, String> resp) {
		String nvp = buildReq(req);
		LOG.info("nvp: " + nvp);
		String respString = HttpUtil.post(UpmpConfig.UPMP_TEST ? UpmpConfig.TRADE_URL_TEST : UpmpConfig.TRADE_URL, nvp);
		LOG.info("respString: " + respString);
		if (!verifyResponse(respString, resp)) {
			return false;
		}
		if (resp.containsKey(UpmpConfig.RESPONSE_CODE)
				&& UpmpConfig.RESPONSE_CODE_SUCCESS.equals(resp.get(UpmpConfig.RESPONSE_CODE))) {
			return true;
		}
		return false;
	}

	/**
     * 交易接口处理
     * 
     * @param req
     *            请求要素
     * @param resp
     *            应答要素
     * @return 是否成功
     */
    public static String tradeToUpmp(Map<String, String> req, Map<String, String> resp) {
        String nvp = buildReq(req);
        LOG.info("nvp: " + nvp);
        String respString = HttpUtil.post(UpmpConfig.UPMP_TEST ? UpmpConfig.TRADE_URL_TEST : UpmpConfig.TRADE_URL, nvp);
        LOG.info("respString: " + respString);
        if (!verifyResponse(respString, resp)) {
            return "";
        }
        if (resp.containsKey(UpmpConfig.RESPONSE_CODE)
                && UpmpConfig.RESPONSE_CODE_SUCCESS.equals(resp.get(UpmpConfig.RESPONSE_CODE))) {
            return respString;
        }
        return "";
    }

    
	/**
	 * 交易查询处理
	 * 
	 * @param req
	 *            请求要素
	 * @param resp
	 *            应答要素
	 * @return 是否成功
	 */
	public static boolean query(Map<String, String> req, Map<String, String> resp) {
		String nvp = buildReq(req);
		LOG.info("nvp: " + nvp);
		String respString = HttpUtil.post(UpmpConfig.UPMP_TEST ? UpmpConfig.QUERY_URL_TEST : UpmpConfig.QUERY_URL, nvp);
		LOG.info("respString: " + respString);
		return verifyResponse(respString, resp);
	}

	/**
	 * 拼接保留域
	 * 
	 * @param req
	 *            请求要素
	 * @return 保留域
	 */
	public static String buildReserved(Map<String, String> req) {
		StringBuilder merReserved = new StringBuilder();
		merReserved.append("{");
		merReserved.append(UpmpCore.createLinkString(req, false, true));
		merReserved.append("}");
		return merReserved.toString();
	}

	/**
	 * 拼接请求字符串
	 * 
	 * @param req
	 *            请求要素
	 * @return 请求字符串
	 */
	public static String buildReq(Map<String, String> req) {
		// 除去数组中的空值和签名参数
		Map<String, String> filteredReq = UpmpCore.paraFilter(req);
		// 生成签名结果
		String signature = UpmpCore.buildSignature(filteredReq);

		// 签名结果与签名方式加入请求提交参数组中
		filteredReq.put(UpmpConfig.SIGNATURE, signature);
		filteredReq.put(UpmpConfig.SIGN_METHOD, UpmpConfig.SIGN_TYPE);

		return UpmpCore.createLinkString(filteredReq, false, true);
	}

	/**
	 * 异步通知消息验证
	 * 
	 * @param para
	 *            异步通知消息
	 * @return 验证结果
	 */
	public static boolean verifySignature(Map<String, String> para) {
		String respSignature = para.get(UpmpConfig.SIGNATURE);
		// 除去数组中的空值和签名参数
		Map<String, String> filteredReq = UpmpCore.paraFilter(para);
		String signature = UpmpCore.buildSignature(filteredReq);
		if (null != respSignature && respSignature.equals(signature)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 应答解析
	 * 
	 * @param respString
	 *            应答报文
	 * @param resp
	 *            应答要素
	 * @return 应答是否成功
	 */
	private static boolean verifyResponse(String respString, Map<String, String> resp) {
		if (respString != null && !"".equals(respString)) {
			// 请求要素
			Map<String, String> para;
			try {
				para = UpmpCore.parseQString(respString);
			} catch (Exception e) {
				return false;
			}
			boolean signIsValid = verifySignature(para);

			resp.putAll(para);

			if (signIsValid) {
				return true;
			} else {
				return false;
			}

		}
		return false;
	}
}

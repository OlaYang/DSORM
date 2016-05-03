package com.meiqi.openservice.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.action.login.thirdLogin.Login3rdAction;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.util.LogUtil;
import com.meiqi.util.wechat.WXBizMsgCrypt;
import com.meiqi.wechat.service.IWeChatAuthorizerService;
import com.meiqi.wechat.service.IWeChatMenuService;
import com.meiqi.wechat.tools.OpenWeChat;
import com.meiqi.wechat.tools.PublicWeChat;
import com.meiqi.wechat.tools.WeChatConfig;

/**
 * 微信对外接口
 * 
 * @author duanran
 *
 */

@Controller
public class OpenWeChatController {
	@Autowired
	private OpenWeChat openWeChat;
	@Autowired
	private PublicWeChat publicWeChat;
	@Autowired
	private Login3rdAction login3rdAction;
	@Autowired
	private IWeChatAuthorizerService weChatAuthorizerService;
	@Autowired
	private IMemcacheAction memcacheAction;

	/**
	 * 微信登录m站
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */

	@RequestMapping("/wx/open/login")
	@ResponseBody
	public String openLogin(HttpServletRequest request,
			HttpServletResponse response)  {
		try {
			String code = request.getParameter("code");
			String state = request.getParameter("state");
			if (null == state) {
				state = "";
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("code", code);
			paramMap.put("state", state);
			Map<String, String> map = login3rdAction.wechatLogin(paramMap);

			// 检查是否有参数
			if (null == map) {
				return "获取用户信息失败！";
			} else {
				if (map.containsKey("redirect")) {
					// 获取需要跳转的url
					String redirectUrl = SysConfig.getWeChatValue(map
							.get("redirect"));

					// 包含条件分隔符表示需要进行条件判断
					if (redirectUrl.contains("$")) {
						// 先切割出每个条件和对应的结果
						String[] redirectUrlArray = redirectUrl.split("#");
						redirectUrl = null;
						for (String tempRedirectUrl : redirectUrlArray) {
							// 循环切割每个case对应的条件和结果，进行条件判断，直到满足条件
							String[] tempArray = tempRedirectUrl.split("\\$");
							String[] tempArrayCase = tempArray[0].split("=");
							if (tempArrayCase[1].equals(map
									.get(tempArrayCase[0]))) {
								redirectUrl = tempArray[1];
								break;
							}
						}
					}

					if (null == redirectUrl || "".equals(redirectUrl.trim())) {
						return "没有找到满足条件的页面！";
					} else {
						Iterator iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String key = entry.getKey().toString();
							if (redirectUrl.contains(key)) {
								redirectUrl = redirectUrl.replaceAll("\\{"
										+ key + "\\}", entry.getValue()
										.toString());
							}
						}
						response.sendRedirect(redirectUrl);
						return null;
					}
				} else {// 兼容最早的我要报备页面。
					if (map.containsKey("user_id")) {
						String userId = map.get("user_id");
						response.sendRedirect("http://m.youjiagou.com/mRemark/?userid="
								+ userId);
						return null;
					} else {
						return "未找到有效的用户id！";
					}
				}
			}
		} catch (Exception e) {
			StringBuffer url = request.getRequestURL();
			if (request.getQueryString() != null) {
			  url.append("?");
			  url.append(request.getQueryString());
			}
			LogUtil.error("wechat login error url = "+url.toString());
			return "系统错误！";
		}

	}

	/**
	 * 获取js调用配置
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/wx/open/getJsSignature", method = RequestMethod.GET)
	@ResponseBody
	public String getJsSignature(HttpServletRequest request,
			HttpServletResponse response) {
		ResponseInfo responseData = new ResponseInfo();
		String sourceUrl = request.getParameter("sourceUrl");
		if (null == sourceUrl) {
			responseData.setCode(DsResponseCodeData.ERROR.code);
			responseData.setDescription("缺少sourceUrl参数！");
			return JSON.toJSONString(responseData);
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("url", sourceUrl);
		paramMap = publicWeChat.getSignature(paramMap);
		// 封装成规则引擎数据格式返回
		responseData.setCode(DsResponseCodeData.SUCCESS.code);
		responseData.setDescription(DsResponseCodeData.SUCCESS.description);
		responseData.setObject(paramMap);
		return JSON.toJSONString(responseData);
	}

	/**
	 * 开放平台第三方公众号授权事件推送
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 * @throws AesException
	 */
	@RequestMapping(value = "/wx/open/event/authorize", method = RequestMethod.POST)
	public String acceptAuthorizeEvent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 从配置文件中获取与微信平台约定的token
		String token = SysConfig.getValue("open_third_token");
		// 获取微信推送过来的参数
		String nonce = request.getParameter("nonce");
		String timestamp = request.getParameter("timestamp");
		String signature = request.getParameter("signature");
		String msgSignature = request.getParameter("msg_signature");
		// 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息
		if (StringUtils.isNotBlank(msgSignature)) {
			// 调用方法进行验签
			boolean isValid = checkSignature(token, signature, timestamp, nonce);
			// 验证签名通过进入业务设置
			if (isValid) {
				// 获取postdata数据
				StringBuilder sb = new StringBuilder();
				BufferedReader in = request.getReader();
				String line;
				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
				// 获取出来的为xml格式字符串
				String xml = sb.toString();
				// 从配置文件中读取与微信约定的消息加密key
				String encodingAesKey = SysConfig.getValue("open_third_key");
				// 从xml报文中获取公众号appid（因为微信托送过来的appid是没有加密的所以可以直接获取使用）
				String appId = getAuthorizerAppidFromXml(xml);
				// 调用微信sdk提供的 解密类
				WXBizMsgCrypt wXBizMsgCrypt = new WXBizMsgCrypt(token,
						encodingAesKey, appId);
				// 进行解密xml，解密出的xml赋值
				xml = wXBizMsgCrypt.DecryptMsg(msgSignature, timestamp, nonce,
						xml);
				Document doc = DocumentHelper.parseText(xml);
				Element rootElt = doc.getRootElement();
				String ticket = rootElt.elementText("ComponentVerifyTicket");
				// 获取ticket
				if (StringUtils.isNotBlank(ticket)) {
					// 将微信推送过来的ticket存入缓存
					LogUtil.info("accept wechat third appid=" + appId
							+ " ticket=" + ticket);
					memcacheAction.putCache(WeChatConfig.weChatThirdTicket,
							ticket);
				}
			}
		}
		return "success";
	}

	/**
	 * 验证加密
	 * 
	 * @param token
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	private boolean checkSignature(String token, String signature,
			String timestamp, String nonce) {
		// 声明一个变量，标记是否验证加密成功，默认失败
		boolean flag = false;
		// 对必要参数进行空值判断
		if (null != signature && !"".equals(signature) && null != timestamp
				&& !"".equals(timestamp) && null != nonce && !"".equals(nonce)) {
			// 声明一个数组存放参数
			String[] ss = new String[] { token, timestamp, nonce };
			// 对数组进行自然派讯
			Arrays.sort(ss);
			// 将排序的结果按顺序拼接成字符串
			String tempSign = ss[0] + ss[1] + ss[2];
			// 进行sha1加密
			tempSign = DigestUtils.sha1Hex(tempSign);
			// 判断加密是否与微信的加密结果一致
			if (tempSign.equals(signature)) {
				// 加密一致，设置验证结果为true
				flag = true;
			}
		}
		// 返回验证结果标记
		return flag;
	}

	/**
	 * 获取授权的Appid
	 * 
	 * @param xml
	 * @return
	 */
	private String getAuthorizerAppidFromXml(String xml) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
			String toUserName = rootElt.elementText("AppId");
			return toUserName;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 授权成功通知
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/wx/open/event/authorizeSuccess")
	@ResponseBody
	public String authorizeSuccess(HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	/**
	 * 获取一键授权url地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/wx/open/toAuthorize")
	@ResponseBody
	public String toAuthorize(HttpServletRequest request,
			HttpServletResponse response) {
		return weChatAuthorizerService.getAuthorizerUrl();
	}

}

package com.meiqi.liduoo.fastweixin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.liduoo.controller.ClientManager;
import com.meiqi.liduoo.fastweixin.util.MessageUtil;

/**
 * 微信公众平台交互操作基类，提供几乎所有微信公众平台交互方式 基于springmvc框架，方便使用此框架的项目集成
 *
 * @author peiyu
 */
@Controller
public abstract class WeixinControllerSupport extends WeixinSupport {

	/**
	 * 绑定微信服务器
	 *
	 * @param request
	 *            请求
	 * @return 响应内容
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	protected final String bind(HttpServletRequest request) {
		channelId = checkChannelId(request);
		if (isLegal(request)) {
			// 绑定微信服务器成功R
			return request.getParameter("echostr");
		} else {
			// 绑定微信服务器失败
			LogUtil.warn("绑定微信服务器失败:URL=" + request.getRequestURL());
			return "";
		}
	}

	/**
	 * 微信消息交互处理
	 *
	 * @param request
	 *            http 请求对象
	 * @param response
	 *            http 响应对象
	 * @throws ServletException
	 *             异常
	 * @throws IOException
	 *             IO异常
	 */
	@RequestMapping(method = RequestMethod.POST)
	protected final void process(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> reqMap = null;
		try {
			channelId = checkChannelId(request);
			// 注意：isLegal中的getToken方法，需要依赖上面的channelId
			if (!isLegal(request)) {
				throw new Exception("请求非法，请检查签名相关配置是否正确.");
			}
			long begin = System.currentTimeMillis();
			Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
			cookie.setPath("/");
			response.addCookie(cookie);

			reqMap = MessageUtil.parseXml(request, getToken(), getAppId(), getAESKey());

			String result = processRequest(request, reqMap);
			// 设置正确的 content-type 以防止中文乱码
			response.setContentType("text/xml;charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(result);
			writer.close();

			long end = System.currentTimeMillis();
			long time = end - begin;
			if (time > 500) {
				LogUtil.error("WeixinController reqInfo slow,execute time:" + time);
				LogUtil.error("WeixinController reqInfo slow,params:" + reqMap);
			} else {
				LogUtil.info("WeixinController reqInfo,execute time:" + time);
			}
		} catch (IOException e1) {
			throw e1;
		} catch (Exception ex) {
			LogUtil.error("WeixinController, execute error:" + ex.getMessage());
			LogUtil.error("WeixinController reqInfo slow,params:" + reqMap);
			ex.printStackTrace();
		}
	}

	/**
	 * 根据request参数和Session等确定当前Request关联的渠道ID
	 * 
	 * @param request
	 * @return
	 */
	private int checkChannelId(HttpServletRequest request) {
		int channelId = -1;
		String cid = request.getParameter("cid");
		if (StringUtils.isEmpty(cid)) {
			Object obj = request.getSession().getAttribute("CURRENT_CHANNEL_ID");
			cid = obj == null ? "-1" : obj.toString();
		}
		if (StringUtils.isEmpty(cid)) {
			cid = "-1";
			// TODO:直接根据微信发送消息中的FromUserName，从DB中获取对应设置的微信渠道ID。
		}
		channelId = Integer.valueOf(cid);
		request.getSession().setAttribute("CURRENT_CHANNEL_ID", channelId);
		ClientManager.setChannelId(channelId);

		return channelId;
	}

}
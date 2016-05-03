/**
 * 
 */
package com.meiqi.liduoo.wechat.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.liduoo.base.constant.ServiceConstants;
import com.meiqi.liduoo.base.services.IChannelService;
import com.meiqi.liduoo.base.services.ILiduooDataService;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.MapUtil;
import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.message.TextMsg;
import com.meiqi.liduoo.fastweixin.message.req.BaseReq;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * @author FrankGui 2015年12月17日
 */
@Service
public abstract class AbstractWeChatService {
	@Autowired
	private ILiduooDataService dataService;

	@Autowired
	private IMemcacheAction memcacheService;

	@Autowired
	private IChannelService channelService;

	/**
	 * 默认微信事件对应的规则
	 * 
	 * @return
	 */
	public Map<String, String> getDefaultHandleService() {
		String key = "CHANNEL_EVENT_RULE_DEFAULT";
		Map<String, String> retMap = (Map<String, String>) memcacheService.getCache(key);
		if (retMap == null) {
			ServiceReqInfo serviceInfo = new ServiceReqInfo();
			serviceInfo.setServiceName(ServiceConstants.SRV_GET_CHANNEL_EVENT_RULE);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("channelid", 0);
			param.put("type", ServiceConstants.PARM_TYPE_DEFAULT);// 4代表dataPage登录请求
			serviceInfo.setParam(param);
			serviceInfo.setNeedAll("1");

			List<Map> ruleList = dataService.getListData(serviceInfo);

			retMap = new HashMap<String, String>();
			for (Map map : ruleList) {
				retMap.put((String) map.get("feventkey"), (String) map.get("fservicename"));
			}
			memcacheService.putCache(key, retMap);
		}
		return retMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liduoo.wechat.services.IChannelService#getEventService(int,
	 * java.lang.String)
	 */

	@SuppressWarnings("unchecked")
	public String getHandleService(int channelId, String eventKey) {
		String key = "CHANNEL_EVENT_RULE_" + channelId;
		eventKey = eventKey.toUpperCase();
		Map<String, String> retMap = (Map<String, String>) memcacheService.getCache(key);
		if (retMap == null) {
			ServiceReqInfo serviceInfo = new ServiceReqInfo();
			serviceInfo.setServiceName(ServiceConstants.SRV_GET_CHANNEL_EVENT_RULE);// LDO_BUV1_channelEventRule
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("channelid", channelId);
			param.put("type", ServiceConstants.PARM_TYPE_DEFAULT);// 4代表dataPage登录请求
			serviceInfo.setNeedAll("1");
			serviceInfo.setParam(param);

			List<Map> ruleList = dataService.getListData(serviceInfo);

			retMap = new HashMap<String, String>();
			for (Map map : ruleList) {
				retMap.put((String) map.get("feventkey"), (String) map.get("fservicename"));
			}
			memcacheService.putCache(key, retMap);
		}
		String ruleService = retMap.get(eventKey);
		if (StringUtils.isEmpty(ruleService)) {
			ruleService = getDefaultHandleService().get(eventKey);
			LogUtil.warn("未找到处理规则:[" + channelId + "," + eventKey + "],查找默认规则....");
		}
		if (StringUtils.isEmpty(ruleService)) {
			LogUtil.error("未找到默认处理规则:[" + channelId + "," + eventKey + "]，请询问Administrator确认配置是否正确....");
		}
		LogUtil.info("找到处理规则:[" + channelId + "," + eventKey + "]为:" + ruleService);
		return ruleService;
	}

	protected BaseMsg invokeService(BaseReq msg, String eventKey, Map<String, Object> reqMap) {
		String workingKey = eventKey + msg.getFromUserName() + msg.getToUserName() + msg.getMsgType();
		if (reqMap != null) {
			workingKey = workingKey + reqMap.get("locationX") + reqMap.get("locationY");
		}
		String resultKey = workingKey + "-result";
		BaseMsg retMsg = null;//new TextMsg("");
		try {
			if ("processing".equals(memcacheService.getCache(workingKey))) {
				return retMsg;// 排重，同样的消息，一分钟内不处理
			}
			memcacheService.putCache(workingKey, "processing", 60000);

			BaseMsg cacheResult = (BaseMsg) memcacheService.getCache(resultKey);
			if (cacheResult != null) {
				return cacheResult;
			}
			String handleService = this.getHandleService(msg.getChannelId(), eventKey);
			if (StrUtil.isBlank(handleService)) {
				if (CommonUtils.toBoolean(channelService.getChannelProperty(msg.getChannelId(), "WX_DEBUG_MODE"))) {
					retMsg = new TextMsg(
							"未找到默认处理规则:[" + msg.getChannelId() + "," + msg + "]，请询问Administrator确认配置是否正确....");
				}

				return retMsg;
			}

			// --先直接执行现在的Get规则处理-------------
			ServiceReqInfo serviceInfo = new ServiceReqInfo();
			serviceInfo.setServiceName(handleService);// LDO_BUV1_channelEventRule
			Map<String, Object> param = new HashMap<String, Object>();
			param.putAll(MapUtil.isNullOrEmpty(reqMap) ? msg.toMap() : reqMap);
			param.put("type", ServiceConstants.PARM_TYPE_DEFAULT);// 4代表dataPage登录请求
			serviceInfo.setNeedAll("1");
			serviceInfo.setParam(param);

			try {
				Map<String, Object> rule = dataService.getOneRow(serviceInfo);
				LogUtil.info("[handleEvent] rule=" + rule);
				// 返回参数矫正
				rule = adjustReturnParameters(rule);

				rule.put("toUserName", msg.getToUserName());
				rule.put("fromUserName", msg.getFromUserName());
				rule.put("createTime", System.currentTimeMillis());

				if (StringUtils.isEmpty((String) rule.get("msgType"))) {
					rule.put("msgType","text");
					if(StringUtils.isEmpty((String) rule.get("content")))
					{
						LogUtil.warn("Failed to initialize new BaseMsg object due to NO VALID CONTENT");
						return retMsg;
					}
				}
				Class clazz = CommonUtils.getMsgClass((String) rule.get("msgType"));
				// JSONUtil.toBean(JSON.toJSONString(rule), clazz);
				BaseMsg msgReturn = (BaseMsg) clazz.newInstance();
				// BaseMsg msgReturn =
				// (BaseMsg)JSONUtil.toBean(JSON.toJSONString(rule), clazz);;
				CommonUtils.mapToBean(rule, msgReturn);
				if (msgReturn.isValidMsg()) {
					retMsg = msgReturn;
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.error("Failed to initialize new BaseMsg object due to " + e.getMessage());
				if (CommonUtils.toBoolean(channelService.getChannelProperty(msg.getChannelId(), "WX_DEBUG_MODE"))) {
					retMsg = new TextMsg("出错了：" + e.getMessage());
				}
			}
		} finally {
			memcacheService.removeCache(workingKey);
			memcacheService.putCache(resultKey, retMsg, 120000);
		}

		return retMsg;
	}

	/**
	 * @param rule
	 * @return
	 */
	private Map<String, Object> adjustReturnParameters(Map<String, Object> rule) {
		if (rule.containsKey("MsgType") && !rule.containsKey("msgType")) {
			rule.put("msgType", rule.get("MsgType"));
		}
		return rule;
	}

}

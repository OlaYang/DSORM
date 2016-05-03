package com.meiqi.liduoo.wechat.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.liduoo.base.services.IChannelService;
import com.meiqi.liduoo.base.services.ILiduooDataService;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.fastweixin.api.BaiduMapAPI;
import com.meiqi.liduoo.fastweixin.message.Article;
import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.message.NewsMsg;
import com.meiqi.liduoo.fastweixin.message.TextMsg;
import com.meiqi.liduoo.fastweixin.message.req.BaseReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.ImageReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.LinkReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.LocationReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.TextReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.VideoReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.VoiceReqMsg;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;
import com.meiqi.liduoo.wechat.services.IFansService;
import com.meiqi.liduoo.wechat.services.IMessageService;

/**
 * 微信消息回复处理服务：针对用户消息的处理
 * 
 * @author FrankGui
 * @date 2015年12月5日 上午10:28:04
 */
@Service("messageService")
public class MessageServiceImpl extends AbstractWeChatService implements IMessageService {
	@Autowired
	private IFansService fansService;
	@Autowired
	private IChannelService channelService;
	@Autowired
	private ILiduooDataService dataService;
	@Autowired
	private IMemcacheAction memcacheService;

	@Override
	public BaseMsg handleDefaultMsg(BaseReqMsg msg) {
		LogUtil.info("MessageServiceImpl.handleDefaultMsg: msg="+msg);
		return invokeService(msg, "MESSAGE_" + msg.getMsgType(),null);
	}

	@Override
	public BaseMsg handleTextMsg(TextReqMsg msg) {
		BaseMsg replymsg = null;
		LogUtil.info("用户输入关键字：TextReqMsg.getContent()="+msg.getContent());
		if (msg.getContent().indexOf("附近") >= 0) {
			// 处理LBS功能
			replymsg = getLbsResponse(msg);
		}
		if (replymsg == null) {
			replymsg = handleDefaultMsg(msg);
		}
		return replymsg;
	}

	/**
	 * @param msg
	 * @return
	 */
	private BaseMsg getLbsResponse(TextReqMsg msg) {
		// Java方式获取LBS回复信息
		LogUtil.info("MessageServiceImpl.getLbsResponse: msg=" + msg);
		String isvId = channelService.getChannelProperty(msg.getChannelId(), "CHANNEL_ISV_ID");
		String keyword = msg.getContent();
		String distance = null;
		Pattern p = Pattern.compile("(\\d+)(公里)|(\\d+\\.\\d+)(米)|(\\d+)(米)|(\\d+\\.\\d+)(公里)");
		Matcher m = p.matcher(keyword);
		if (m.find()) {
			String d = m.group(0);
			if (d.endsWith("公里")) {
				distance = d.substring(0, d.length() - 2);
				distance = Integer.valueOf(distance) * 1000 + "";
			} else if (d.endsWith("米")) {
				distance = d.substring(0, d.length() - 1);
			}
		}
		if (StrUtil.isBlank(distance)) {
			distance = channelService.getIsvSetting(Integer.valueOf(isvId), "LBS_DEF_NEARBY");
			if (StrUtil.isBlank(distance)) {
				distance = "1000";
			} else {
				distance = Integer.valueOf(distance) * 1000 + "";
			}
		}
		String oldTalkId = addTalkMsg(msg, isvId, "1", keyword, null);

		Map<String, Object> fans = fansService.getFansByOpenId(msg.getFromUserName(), true, msg.getChannelId() + "");
		String locationStr = (String) fans.get("fcurlocation");
		if (StringUtils.isEmpty(locationStr) || "0,0".equals(locationStr)) {
			addTalkMsg(msg, isvId, "2", "您未设置同意微信获取位置信息或未开启定位服务！请点击右上角头像，将“提供位置信息”开关打开。", oldTalkId);
			TextMsg textMsg = new TextMsg(msg.getFromUserName(), msg.getToUserName());
			textMsg.setContent("您未设置同意微信获取位置信息或未开启定位服务！请点击右上角头像，将“提供位置信息”开关打开。");
			return textMsg;
		}
		// {"coords":"104.07306610972,30.550312339097","updateTime":1453429927}
		Map<String, Object> location = JSONUtil.toMap(locationStr);
		if ((System.currentTimeMillis() - (Long) location.get("updatetime")) >= 3600 * 24*1000) {
			addTalkMsg(msg, isvId, "2", "您未设置同意微信获取位置信息或未开启定位服务！请点击右上角头像，将“提供位置信息”开关打开。", oldTalkId);
			TextMsg textMsg = new TextMsg(msg.getFromUserName(), msg.getToUserName());
			textMsg.setContent("您未设置同意微信获取位置信息或未开启定位服务！请点击右上角头像，将“提供位置信息”开关打开。");
			return textMsg;
		}
		return searchBaiduNearby(msg, isvId, distance, (String) location.get("coords"), oldTalkId);
	}

	@Override
	public BaseMsg handleImageMsg(ImageReqMsg msg) {
		return handleDefaultMsg(msg);
	}

	@Override
	public BaseMsg handleVoiceMsg(VoiceReqMsg msg) {
		return handleDefaultMsg(msg);
	}

	@Override
	public BaseMsg handleVideoMsg(VideoReqMsg msg) {
		return handleDefaultMsg(msg);
	}

	@Override
	public BaseMsg handleShortVideoMsg(VideoReqMsg msg) {
		return handleDefaultMsg(msg);
	}

	@Override
	public BaseMsg handleLinkMsg(LinkReqMsg msg) {
		return handleDefaultMsg(msg);
	}

	@Override
	public BaseMsg handleLocationMsg(LocationReqMsg msg) {
		String isvId = channelService.getChannelProperty(msg.getChannelId(), "CHANNEL_ISV_ID");
		String distance = channelService.getIsvSetting(Integer.valueOf(isvId), "LBS_DEF_NEARBY");
		if (StrUtil.isBlank(distance)) {
			distance = "1000";
		} else {
			distance = Integer.valueOf(distance) * 1000 + "";
		}

		// 记录用户输入，作为用户反馈内容
		// MsgUtils::newFeedback($keyword, $configs["WECHAT_ISV_ID"],
		// $configs["WECHAT_CHANNEL_ID"], "用户反馈", TRUE);
		String oldTalkId = addTalkMsg(msg, isvId, "1", "发送位置:" + msg.getLocationY() + "," + msg.getLocationX(), null);// Java中记录到了talk中，后期改正

		// 把微信发上来的位置转换为百度坐标，否则查询不准
		String rt = BaiduMapAPI.gpsToBaidu(msg.getLocationY() + "," + msg.getLocationX());
		// {"status":0,"result":[{"x":121.35389864479,"y":30.745896679727}]}
		Map<String, Object> ret = (Map<String, Object>) JSONUtil.toMap(rt);
		if ((Integer) ret.get("status") != 0) {
			// MsgUtils::newTalkMsg($configs["WECHAT_ISV_ID"],
			// $configs["WECHAT_CHANNEL_ID"], Constants::MSG_DIRECTION_REPLY,
			// $tousername, $fromusername, "text",
			// "后台错误：{$rt['message']}，url：{$rt['url']}", $talkMsgId);
			addTalkMsg(msg, isvId, "2", "后台错误：" + ret.get("message"), oldTalkId);
			TextMsg textMsg = new TextMsg(msg.getFromUserName(), msg.getToUserName());
			textMsg.setContent("后台错误：" + ret.get("message"));
			return textMsg;
		}
		JSONArray o = (JSONArray) ret.get("result");
		String coords = ((JSONObject) o.get(0)).get("x") + "," + ((JSONObject) o.get(0)).get("y");

		return searchBaiduNearby(msg, isvId, distance, coords, oldTalkId);
	}

	public BaseMsg searchBaiduNearby(BaseReqMsg msg, String isvId, String distance, String coords, String oldTalkId) {
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("location", coords);
		parms.put("radius", distance);
		parms.put("sortby", "distance:1");
		parms.put("filter", "fisvid:" + isvId + "," + isvId + "|fserverid:" + BaiduMapAPI.SERVER_LBS_ID + ","
				+ BaiduMapAPI.SERVER_LBS_ID);
		String ret = BaiduMapAPI.searchNearBy(parms);
		Map<String, Object> retMap = JSONUtil.toMap(ret);

		if ((Integer) retMap.get("status") != 0) {
			addTalkMsg(msg, isvId, "2", "后台错误：" + retMap.get("message"), oldTalkId);
			TextMsg textMsg = new TextMsg(msg.getFromUserName(), msg.getToUserName());
			textMsg.setContent("后台错误：" + retMap.get("message"));
			return textMsg;
		}
		int size = (Integer) retMap.get("size");
		if (size == 0) {
			addTalkMsg(msg, isvId, "2", "没有查询到符合条件的数据！", oldTalkId);
			TextMsg textMsg = new TextMsg(msg.getFromUserName(), msg.getToUserName());
			textMsg.setContent("没有查询到符合条件的数据！");
			return textMsg;
		}

		memcacheService.putCache("LOCATION_" + msg.getFromUserName(), retMap);
		String title = "点击查看周边" + size + "家门店";

		List<Article> list = new ArrayList<Article>();

		String picurl = BaiduMapAPI.getStaticImageUrl(coords, 400, 300, 16);
		String listUrl = LdConfigUtil.getWeb_root_url()+ "/static/mobile/default/html/lbs/lbsShopList.html?from="+ msg.getFromUserName();
		Article artical = new Article();
		artical.setDescription(title);
		artical.setPicUrl(picurl);
		artical.setTitle(title);
		artical.setUrl(listUrl);
		list.add(artical);
		JSONArray contents = (JSONArray) retMap.get("contents");
		for (int i = 0; i < size; i++) {
			artical = new Article();
			JSONObject row = (JSONObject) contents.get(i);
			
			int dist = (Integer) row.get("distance");
			if (dist >= 1000) {
				title = "【" + row.get("fname") + "】 距离" + dist / 1000 + "公里";
			} else {
				title = "【" + row.get("fname") + "】 距离" + dist + "米";
			}
			artical.setTitle(title);
			artical.setDescription("距离" + dist + "米");
			if (row.get("flogo") != null)
				picurl = CommonUtils.getWebImagePath((String) row.get("flogo"));
			else {
				JSONArray location = (JSONArray) row.get("location");

				picurl = BaiduMapAPI.getStaticImageUrl(location.get(0) + "," + location.get(1), 400, 300, 16);
			}
			row.put("flogo", picurl);
			artical.setPicUrl(picurl);
			artical.setUrl(LdConfigUtil.getWeb_root_url()+"/static/mobile/default/html/lbs/lbsShopDetail.html?id="+row.get("uid")+"&from="+msg.getFromUserName());
			list.add(artical);
			CachePool.getInstance().putCacheItem("LOCATION_" + msg.getFromUserName() + "_" + row.get("uid"),
					row.toJSONString());
		}
		CachePool.getInstance().putCacheItem("LOCATION_" + msg.getFromUserName(),JSON.toJSONString(retMap));
		
		artical = new Article();
		artical.setDescription("输入“附近XXX米或附近XX公里”可查询更加精确");
		artical.setTitle("输入“附近XXX米或附近XX公里”可查询更加精确");
		artical.setUrl("");// WP_DOMAIN_URL .
							// "/index.php?r=mob/mobLBS/shopList&from={$fromusername}";

		list.add(artical);

		addTalkMsg(msg, isvId, "2", "【位置消息】", oldTalkId);
		NewsMsg news = new NewsMsg(list);
		news.setFromUserName(msg.getFromUserName());
		news.setToUserName(msg.getToUserName());
		return news;
	}

	private String addTalkMsg(BaseReqMsg msg, String isvid, String direct, String content, String oldTalkId) {
		ServiceReqInfo serviceInfo = new ServiceReqInfo();
		serviceInfo.setServiceName("LDO_HSV1_addTalkMsg");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("fisvid", isvid);
		param.put("fchannelid", msg.getChannelId());
		param.put("fmsgtype", "text");
		param.put("fdirection", direct);// 1-Send,2-Reply
		param.put("fromUserName", msg.getFromUserName());
		param.put("toUserName", msg.getToUserName());
		param.put("fcontent", content);
		param.put("forgmsgid", oldTalkId);

		param.put("type", "4");// 4代表dataPage登录请求
		serviceInfo.setParam(param);
		try {
			Map<String, Object> ret = dataService.getOneRow(serviceInfo);
			return ret.get("new_talkid") == null ? null : ret.get("new_talkid").toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.error(ex.getMessage());
		}

		return null;
	}
}

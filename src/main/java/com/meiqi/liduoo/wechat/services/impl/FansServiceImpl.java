package com.meiqi.liduoo.wechat.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.liduoo.base.constant.ServiceConstants;
import com.meiqi.liduoo.base.services.IChannelService;
import com.meiqi.liduoo.base.services.ILiduooDataService;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.MapUtil;
import com.meiqi.liduoo.fastweixin.api.UserAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;
import com.meiqi.liduoo.fastweixin.util.StrUtil;
import com.meiqi.liduoo.wechat.services.IFansService;

/**
 * 粉丝操作通用接口实现类
 * 
 * @author FrankGui 2015年12月11日
 */
@Service
public class FansServiceImpl implements IFansService {
	@Autowired
	private ILiduooDataService dataService;
	@Autowired
	private IChannelService channelService;
	@Autowired
	private IMushroomAction mushroomAction;

	@Override
	public Map<String, Object> getFansByOpenId(String openid, boolean forceAdd, String appIdOrCid) {
		ServiceReqInfo serviceInfo = new ServiceReqInfo();
		serviceInfo.setServiceName(ServiceConstants.SRV_GET_FANS_INFO);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("open_id", openid);
		param.put("type", ServiceConstants.PARM_TYPE_DEFAULT);// 代表Liduoo登录请求，暂定为6
		serviceInfo.setParam(param);
		Map fansInfo = dataService.getOneRow(serviceInfo);
		if (MapUtil.isNullOrEmpty(fansInfo) && forceAdd) {
			int channelid = -1;
			if (StrUtil.isNumber(appIdOrCid)) {
				channelid = Integer.valueOf(appIdOrCid);
			} else {
				Map<String, Object> cMap = getCommonPropertyByValue("WECHAT_APPID", appIdOrCid);
				if (MapUtils.isNotEmpty(cMap) && cMap.containsKey("flinkid")) {
					channelid = Integer.valueOf((String) cMap.get("flinkid"));
				}
			}
			// 插入数据
			fansInfo = new HashMap<String, Object>();
			fansInfo.put("fopenid", openid);
			setFansInfo(fansInfo, true);
			
			fansInfo = dataService.getOneRow(serviceInfo);
		}
		
		return fansInfo;
	}

	@Override
	public Map<String, Object> getCommonPropertyByValue(String propName, String propValue) {
		ServiceReqInfo serviceInfo = new ServiceReqInfo();
		serviceInfo.setServiceName("LDO_BUV1_tCommonProperties");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("property", propName);
		param.put("value", propValue);
		param.put("type", ServiceConstants.PARM_TYPE_DEFAULT);// 代表Liduoo登录请求，暂定为6
		serviceInfo.setParam(param);
		Map<String, Object> prop = dataService.getOneRow(serviceInfo);

		return prop;
	}

	@Override
	public Map<String, Object> getFansByFansId(int fansid) {
		ServiceReqInfo serviceInfo = new ServiceReqInfo();
		serviceInfo.setServiceName(ServiceConstants.SRV_GET_FANS_INFO);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", fansid);
		param.put("type", ServiceConstants.PARM_TYPE_DEFAULT);// 代表Liduoo登录请求，暂定为6
		serviceInfo.setParam(param);
		Map fansInfo = dataService.getOneRow(serviceInfo);

		return fansInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liduoo.wechat.services.IFansService#syncFansByFansId(int)
	 */
	@Override
	public boolean syncFansByFansId(int fansid, Map<String, Object> fansInfo) {

		return false;
	}

	/**
	 * 同步粉丝信息
	 * 
	 * @see com.meiqi.liduoo.wechat.services.IFansService#syncFansByOpenId(java.lang.
	 *      String)
	 */
	@Override
	public boolean setFansInfo(String openid, GetUserInfoResponse wxUserInfo, String appIdOrCid) {
		boolean isNewFans = false;
		Map<String, Object> fansInfo = getFansByOpenId(openid, true, appIdOrCid);

		// channelService.getChannelProperties((Integer)fansInfo.get("fchannelid"));
		ApiConfig config = channelService.initApiConfig((Integer) fansInfo.get("fchannelid"));
		if (wxUserInfo == null) {
			UserAPI userApi = new UserAPI(config);
			wxUserInfo = userApi.getUserInfo(openid);
			wxUserInfo.verifyWechatResponse(true, config);
		}
		fansInfo.put("fsubscribe", wxUserInfo.getSubscribe());

		if (StrUtil.isNotBlank(wxUserInfo.getNickname())) {
			fansInfo.put("fnickname", wxUserInfo.getNickname());
			fansInfo.put("foauthed", 1);
		} else {
			fansInfo.put("foauthed", 0);
		}
		fansInfo.put("fsex", wxUserInfo.getSex());
		if (StrUtil.isNotBlank(wxUserInfo.getLanguage())) {
			fansInfo.put("flanguage", wxUserInfo.getLanguage());
		}
		if (StrUtil.isNotBlank(wxUserInfo.getProvince())) {
			fansInfo.put("fprovince", wxUserInfo.getProvince());
		}
		if (StrUtil.isNotBlank(wxUserInfo.getCountry())) {
			fansInfo.put("fcountry", wxUserInfo.getCountry());
		}
		if (StrUtil.isNotBlank(wxUserInfo.getCity())) {
			fansInfo.put("fcity", wxUserInfo.getCity());
		}
		if (StrUtil.isNotBlank(wxUserInfo.getHeadimgurl())) {
			fansInfo.put("fheadimgurl", wxUserInfo.getHeadimgurl());
		}
		fansInfo.put("fsubscribetime", wxUserInfo.getSubscribeTime());
		if (StrUtil.isNotBlank(wxUserInfo.getUnionid())) {
			fansInfo.put("funionid", wxUserInfo.getUnionid());
		}
		if (StrUtil.isNotBlank(wxUserInfo.getRemark())) {
			fansInfo.put("fremark", wxUserInfo.getRemark());
		}
		fansInfo.put("fgroupid", wxUserInfo.getGroupid());
		return setFansInfo(fansInfo, isNewFans);
	}

	public boolean setFansInfo(Map<String, Object> fansInfo, boolean isNewFans) {
		com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo actionReqInfo = new com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo();
		actionReqInfo.setServiceName(ServiceConstants.MUSH_SVR_NAME);
		actionReqInfo.setSite_id(ServiceConstants.SRV_DEFAULT_SITE_ID);
		Action action = new Action();
		action.setServiceName(ServiceConstants.SRV_SET_PREFIX + "_t_fans");
		action.setType(isNewFans ? "C" : "U");
		action.setSet(fansInfo);
		if (!isNewFans) {
			Where where = new Where();
			List<SqlCondition> condList = new ArrayList<SqlCondition>();
			SqlCondition cond = new SqlCondition();
			cond.setKey("fopenid");
			cond.setValue(fansInfo.get("fopenid"));
			cond.setOp("=");
			condList.add(cond);
			where.setConditions(condList);
			action.setWhere(where);
		}
		List<Action> actionList = new ArrayList<Action>();
		actionList.add(action);
		 Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actionList);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
		String res1 = mushroomAction.offer(actionReqInfo);

		return CommonUtils.verifyMushroomResult(res1, true);
		// ActionRespInfo respInfo = JSONUtil.toBean(res1,
		// ActionRespInfo.class);

		// return respInfo.getResults()
	}

}

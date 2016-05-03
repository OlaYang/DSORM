package com.meiqi.liduoo.wechat.services;

import java.util.Map;

import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;

/**
 * 粉丝操作通用接口
 * @author FrankGui
 *
 */
public interface IFansService {
	/**
	 * 根据OpenID获取粉丝信息
	 * 
	 * @param openid String 粉丝OpenID
	 * @return 粉丝信息Map
	 */
	public Map<String,Object> getFansByOpenId(String openid,boolean forceAdd,String appIdOrCid);
	/**
	 * 根据粉丝ID获取粉丝信息
	 * 
	 * @param fansid int 粉丝ID
	 * @return 粉丝信息Map
	 */
	public Map<String,Object> getFansByFansId(int fansid);
	
	public boolean syncFansByFansId(int fansid, Map<String, Object> fansInfo);
	public boolean setFansInfo(String openid, GetUserInfoResponse wxUserInfo, String appIdOrCid);
	/**
	 * @param propName
	 * @param propValue
	 * @return
	 */
	Map<String, Object> getCommonPropertyByValue(String propName, String propValue);
}

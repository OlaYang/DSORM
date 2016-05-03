package com.meiqi.liduoo.base.constant;

import com.meiqi.liduoo.base.utils.LdConfigUtil;

/**
 * Liduoo后台使用的常用的Service名字
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午12:50:56
 */
public final class ServiceConstants {
	/**
	 * 规则引掣中的type参数在Liduoo的默认值
	 */
	public final static String PARM_TYPE_DEFAULT = "6";
	public final static String MUSH_SVR_NAME = "MUSH_Offer";
	/**
	 * 礼多噢默认Site ID
	 */
	public final static int SRV_DEFAULT_SITE_ID = 6;

	public final static String SRV_GET_CHANNEL_EVENT_RULE = "LDO_SYSV1_channelEventRule";
	
	public final static String SRV_GET_CHANNEL_INFO = "LDO_HSV1_ChannelInfo";
	
	public final static String SRV_SET_PREFIX = "i2goods2_dev2";//LdConfigUtil.getMushroom_prefix();

	public final static String SRV_GET_FANS_INFO = "LDO_BUV1_tFans";

}

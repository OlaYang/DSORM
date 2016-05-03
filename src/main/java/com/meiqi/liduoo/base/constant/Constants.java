package com.meiqi.liduoo.base.constant;

public final class Constants {
	public static final int MAX_ACT_SORT = 9999;
	public static final double NEW_FANS_DISCOUNT = 0.618D;// 新粉丝砍价、红包加码时的折扣率
	public static final double OLD_FANS_DISCOUNT = 0.618;// 老粉丝砍价、红包加码时的折扣率
	public static final int SELF_ISV = -1;
	public static final int LOGIN_REMEMBER_ME_TIMEOUT = 1209600; // 默认自动登录时间:3600
																	// * 24 *
																	// 14,两周;
	public static final int USER_TYPE_COMMON = 0; // 0－普通注册用户
	public static final int USER_TYPE_ISV = 1; // 1－商户用户
	public static final int USER_TYPE_SYSTEM = 2; // 2 － 系统管理用户

	public static final int USER_BIND_NO = 0; // 0－未绑定
	public static final int USER_BIND_DONE = 1; // 1-已绑定
	public static final int USER_BIND_TO_OTHER = 2; // 2-已转移(绑定到其他用户)

	// ------PROJECT_FILE_TYPE------------
	public static final int FILE_TYPE_LOGO = 0; // 封面
	public static final int FILE_TYPE_QRCODE = 1; // 二维码
	public static final int FILE_TYPE_VOICE = 2; // 语音
	public static final int FILE_TYPE_BARCODE = 3; // 条形码

	public static final int LT_ALL = -1;// 通用，代表所有
	public static final int LT_PROJECT = 0;
	public static final int LT_ISV = 1;
	public static final int LT_APPLICATION = 2;
	public static final int LT_APP_DEVELOPER = 3;
	public static final int LT_PUBLISH_STYLE = 4;
	public static final int LT_PUBLISH_CHANNEL = 5;
	public static final int LT_ISV_CHANNEL = 6; // 企业预定义渠道
	public static final int LT_PUBLISH_SET = 7;
	public static final int LT_CONTENT = 8; // 轻内容
	public static final int LT_USER = 10; // 用户
	// --------一键发布相关的常量--------------------BEGIN--------------------------
	public static final int LT_USER_GROUP = 11; // 用户组
	public static final int LT_MESSGE_CONTENT = 12; // 群发的消息News Item
	public static final int LT_REPLY_RULE = 13; // 回复规则
	public static final int LT_REPLY_RULE_NEWS = 14; // 回复规则的News Item
	public static final int LT_MESSAGE = 15; // 消息
	public static final int LT_MENU = 16; // 菜单

	public static final int LT_FANS = 17; // 粉丝
	public static final int LT_MSISDN_BOOK = 18; // 手机号码簿
	public static final int LT_CONTACTS = 19; // 联系人
	public static final int LT_CONTACTS_FANS = 20; // 联系人和粉丝
	public static final int LT_SYNC_FANS = 21;

	public static final int LT_ACT = 22;
	public static final int LT_ORDER = 23; // 订单
	public static final int LT_ACCOUNT = 24; // 账户，与t_isv_acc_money表关联
	public static final int LT_FLOW_OWNER = 25; // 流量主
	public static final int LT_ACT_CROWD = 26; // 众筹活动
	public static final int LT_FLOW_OWNER_ENCASHMENT = 28; // 流量主提现
}

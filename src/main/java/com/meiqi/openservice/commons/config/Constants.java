package com.meiqi.openservice.commons.config;

import java.util.HashMap;
import java.util.Map;

public interface Constants
{
    /**
     * 用户类型
     */
    String USER_TYPE = "userType";

    /*
     * 用户类型userType 定义：
     * 0=其它 1=优家购 2=爱有窝 3=韩丽商城 4=好莱客商城 5=梦百合
     */
    enum UserType {
        HEMEIJU_USER("优家购", 1), // 优家购用户类型
        LEJJ_USER("爱有窝", 2), // 爱有窝用户类型
        HL_USER("韩丽商城", 3), // 韩丽商城
        HLK_USER("好莱客商城", 4), // 好莱客商城
        MBH_USER("梦百合", 5), // 梦百合
        APP_MENDIANBAO_USER("APP门店宝用户", 6), // APP门店宝用户
        LADP_USER("工作站用户", 7), // 工作站用户（redmine）
        UNKNOWN("", 0);


        private String name;
        private int    index;
        
        // 构造方法
        private UserType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

    };
        
    /**
     * 
     */
    String CODE_TYPE = "codeType";
    
    interface CodeType
    {
        String LEJJ_REGISTER = "1";// 乐家居用户注册
        
        String LEJJ_APPLY = "2";// 乐家居免费申请设计(页面)
        
        String HEMEIJU_REGISTER = "3";// 和美居用户注册
        
        String LEJJ_APPLY_DIALOG = "4";// 乐家居免费申请设计（dialog弹出框）
        
        String LEJJ_GETBACK_PWD = "5";// 乐家居找回密码的验证码
        
        String ADD_DIARY = "6";// 添加装修案例验证码
        
        String SEND_EXPERIENCE_ADDRESS= "7";//发送体验馆地址
    }
    
    //请求来源
    interface SourceFrom
    {
        String PC = "1";// pc端
        
        String APP = "2";// APP
        
        String M = "3";// M站
        
    }
    
    // 注册验证码类型
    Map<String, String> NormalVerifyCodeType = new HashMap<String, String>()
    {
        private static final long serialVersionUID = 1L;
        {
            put(CodeType.LEJJ_REGISTER, "NormalVerifyCodeType_Lejj_Register");
            put(CodeType.LEJJ_APPLY, "NormalVerifyCodeType_Lejj_Apply");
            put(CodeType.HEMEIJU_REGISTER, "NormalVerifyCodeType_Hemeiju_Register");
            put(CodeType.LEJJ_APPLY_DIALOG, "NormalVerifyCodeType_Lejj_Apply_Dialog");
            put(CodeType.LEJJ_GETBACK_PWD, "NormalVerifyCodeType_Lejj_GetBack_Pwd");
            put(CodeType.ADD_DIARY, "NormalVerifyCodeType_Add_Diary");
            put(CodeType.ADD_DIARY, "NormalVerifyCodeType_Add_Diary");
            put(CodeType.SEND_EXPERIENCE_ADDRESS, "NormalVerifyCodeType_SEND_EXPERIENCE_ADDRESS");

            /*
             * 绑定邮件相关验证码codeType 定义： 
             * "BindEmailStep" + bindEmailStep + userType 示例：BindEmailStep11
             */
            for (UserType userType : UserType.values()) {
                for (int bindEmailStep = BindEmailStep.OTHER; bindEmailStep <= BindEmailStep.VERIFY_BINDED_PHONE; bindEmailStep++) {
                    String codeType = BIND_EMAIL_STEP + bindEmailStep + userType.getIndex();
                    put(codeType, codeType);
                }
            }
            
        }
    };
    
    interface VerificationCodeType
    {
        // 验证码类型
        // 0,注册短信
        // 1,找回密码短信
        // 2,更改手机绑定短信（验证旧手机号）
        // 3,银行卡号绑定短信
        // 4,更改手机绑定短信（验证新手机号）
        // 5,订单查询短信
        // 6,折扣码短信
        // 7,邀请码短信
        // 8,店铺信息
        // 9,手机验证登陆
        // 10,优码邀约码
        // 11,优码折扣码
        byte register = 0;
        
        byte getBackPwd = 1;
        
        byte changeBindingPhone = 2;
        
        byte bankCard = 3;
        
        byte modifyBindingPhone = 4;
        
        byte queryOrderPhone =5;
        
        byte discountCode =6;
        
        byte inviteCode =7;
        
        byte storeInfoType =8;
        
        byte phoneLoginType =9;
        
        byte ymInviteCode = 10;
        
        byte ymDiscountCode = 11;
        
        
    }
	
	// 第三方认证方式
	interface LOGIN_3RD{
	    String LOGIN_TYPE_SINA = "sina"; //新浪微博登陆
	    String LOGIN_TYPE_QQ = "qq"; // qq登陆
	    String LOGIN_TYPE_WECHAT="wechat"; //微信登录
	}
	/**
	 * 获取第三方登录的详细类型
	 * */
	public static final String LOGIN_TYPE = "logintype";
	
	interface WebSiteType{
	    /*
	     *项目类别
	     *1：优加购
	     *2：乐家居
	     *3：韩丽商城
	     *4：好莱客
	     *5：梦百合
	     */
        byte YojgSiteType  = 1;
        byte LejjSiteType  = 2;
        byte HanLiSiteType = 3;
        byte HaoLkSiteType = 4;
        byte MengBHSiteType = 5;
	}
	
	interface SmsType{
	    /*
	     * 短信验证码0-19
	     *0：注册短信验证
	     *1：找回密码验证
	     *2：更改手机绑定（验证旧手机）
	     *3：更改手机绑定（验证新手机）
	     *4：手机登陆验证 
	     *5：银行卡绑定
	     *6：行业专家驻第二步
	     *20：订单折扣码
	     *21：提交订单通知
	     *22：邀约码 
	     *23：体验馆地址
	     *24:设计师入驻通过
	     *25：设计师入驻驳回
	     *26：免费预约设计师
	     *27：免费量尺
	     *28：免费设计
	     *29：发送优惠券
	     *30：微信砍价活动
	     *31：降价通知
	     *32：买赠红包
	     *33：订单通知
	     *34:商品出库通知
	     *35:门店宝审核通过通知
	     *36:门店宝审核拒绝通知
	     *37:门店宝地址
	     *38:门店宝红包
	     */
        byte register       = 0;
        byte getBackPwd     = 1;
        byte verifyOldPhone = 2;
        byte verifyNewPhone = 3;
        byte phoneLogin     = 4;
        byte bindBank       = 5;
        byte enter          = 6;
        byte discountCode   = 20;
        byte submitOrder    = 21;
        byte inviteCode     = 22;
        byte experienceAddr = 23;
        byte enterSuccess   = 24;
        byte enterFail      = 25;
        byte freeInvite     = 26;
        byte freeMeasure    = 27;
        byte freeDesign     = 28;
        byte freeCoupon     = 29;
        byte weiXinKanJia   = 30;
        byte depreciate   = 31;
        byte paygiveredpacketaction   = 32;
        byte orderaffirm   = 33;
        byte deliver   = 34;
        byte storeVerifyPassInform = 35;
        byte storeVerifyRefuseInform = 36;
        byte storeAddress = 37;
        byte storeRedPacketaction = 38;
        
	}
	
    // 各平台 memache缓存 key前缀定义
    interface memcacheKeyPrefix {
        // ipad
        String ipad    = "ipad_";
        // iphone
        String iphone  = "iphone_";
        // addroid
        String android = "android_";
        
    }
    
    /*
     * 角色
     */
    interface UserRoleId {
        int ANONYMOUS = 1;
        // 2设计师
        int DESIGNER = 2;
        // 3导购
        int SHOPPER  = 3;
        // 4装修公司
        int COMPANY  = 4;
        // 12地推人员
        int PUSHER   = 12;
    }

    /*
     * site_id 定义： 0=优加购/乐家居 1=韩丽商城 2=好莱客商城 3=梦百合
     */
    interface SiteId {
        // 优加购/乐家居
        byte YJG = 0;
        byte LJJ = 0;
        // 韩丽商城
        byte HL  = 1;
        // 好莱客商城
        byte HLk = 2;
        // 梦百合
        byte MBH = 3;
    }

    String BIND_EMAIL_STEP = "BindEmailStep";
    /*
     * bindEmailStep 
     * 定义： 0=其它 1=验证绑定邮箱 2=验证已验证邮箱 3=验证新修改邮箱 4=验证登录密码 5=绑定邮箱 6=验证已验证手机
     */
    interface BindEmailStep {
        // 0=其它
        byte   OTHER                    = 0;
        // 1=验证绑定邮箱
        byte   VERIFY_BIND_EMAIL        = 1;
        String VERIFY_BIND_EMAIL_DESC   = "验证新邮箱";
        // 2=验证已验证邮箱
        byte   VERIFY_BINDED_EMAIL      = 2;
        String VERIFY_BINDED_EMAIL_DESC = "验证已验证邮箱";
        // 3=验证新修改邮箱
        byte   VERIFY_MODIFY_EMAIL      = 3;
        String VERIFY_MODIFY_EMAIL_DESC = "验证修改邮箱";
        // 4=验证登录密码
        byte   VERIFY_LOGIN_PWD         = 4;
        // 5=绑定邮箱
        byte   BIND                     = 5;
        // 6=验证已验证手机
        byte   VERIFY_BINDED_PHONE      = 6;
    }
    
    /*
     * 项目类别
     */
    interface WebSite {
        // 优家购
        byte YJG = 0;
        // 韩丽商城
        byte HL  = 1;
        // 好莱客
        byte HLk = 2;
        // 梦百合
        byte MBH = 3;
        
        // 爱有窝
        byte AUW = 4;
    }
    
    /*
     * 来源
     */
    interface UserFrom {
        // 0 默认优家购WEB
        int DEFAULT     = 0;
        
        // 0-优家购WEB
        int YJG_WEB     = 0;
        
        // 1-爱有窝WEB
        int AUW_WEB =   1;
        
        // 2-优家购安卓
        int YJG_ANDROID = 2;
        
        // 3-优家购IPAD
        int YJG_IPAD    = 3;
        
        // 4-优家购IPHONE
        int YJG_IPHONE  = 4;
        
        // 5-优家购M站
        int YJG_WAP  = 5;
        
        // 6-爱有窝M站
        int AUW_WAP  = 6;
        
    }
    
  //sex（性别 0保密 1男 2女）
    enum UserSex {
        unknow,
        male,
        female
    }
    

}

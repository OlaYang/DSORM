package com.meiqi.liduoo.fastweixin.message.req;

public final class EventType {

    public static final String SUBSCRIBE       = "subscribe";
    public static final String UNSUBSCRIBE     = "unsubscribe";
    public static final String CLICK           = "CLICK";
    public static final String VIEW            = "VIEW";
    public static final String LOCATION        = "LOCATION";
    public static final String SCAN            = "SCAN";
    public static final String SCANCODEPUSH    = "scancode_push";
    public static final String SCANCODEWAITMSG = "scancode_waitmsg";
    public static final String PICSYSPHOTO     = "pic_sysphoto";
    public static final String PICPHOTOORALBUM = "pic_photo_or_album";
    public static final String PICWEIXIN       = "pic_weixin";
    public static final String LOCATIONSELECT  = "location_select";
    public static final String TEMPLATESENDJOBFINISH  = "TEMPLATESENDJOBFINISH";
    public static final String MASSSENDJOBFINISH="MASSSENDJOBFINISH";
    
    /**卡券审核通过事件*/
    public static final String CARD_PASS_CHECK="card_pass_check";
    /**卡券审核未通过事件*/
    public static final String CARD_NOT_PASS_CHECK="card_not_pass_check";
    /**用户领取卡券事件*/
    public static final String USER_GET_CARD="user_get_card";
    /**删除卡券事件*/
    public static final String USER_DELETE_CARD="user_del_card";
    /**用户核销卡券事件*/
    public static final String USER_CONSUME_CARD="user_consume_card";
    /**买单事件推送*/
    public static final String USER_PAY_FROM_PAY_CELL="user_pay_from_pay_cell";
    /**进入会员卡事件推送*/
    public static final String USER_VIEW_CARD="user_view_card";
    /**从卡券进入公众号会话事件推送*/
    public static final String USER_ENTER_CARD="user_enter_session_from_card";
    /**会员卡内容更新事件：当用户的会员卡积分余额发生变动时，微信会推送事件告知开发者。 推送XML数据包示例：*/
    public static final String UPDATE_MEMBER_CARD="update_member_card";
    /**库存报警事件*/
    public static final String CARD_SKU_REMIND="card_sku_remind";
   

    private EventType() {
    }

}

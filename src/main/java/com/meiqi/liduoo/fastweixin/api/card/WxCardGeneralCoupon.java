package com.meiqi.liduoo.fastweixin.api.card;

import org.json.JSONException;

/**
 *
 * @author jackylian
 */
public class WxCardGeneralCoupon extends WxCard
{
    public WxCardGeneralCoupon() throws JSONException
    {
        init("GENERAL_COUPON");
    }
    
    public void setDefaultDetail(String detail) throws JSONException
    {
        m_data.put("default_detail", detail);
    }
    
    public String getDefaultDetail()
    {
        return m_data.optString("default_detail");
    }
}

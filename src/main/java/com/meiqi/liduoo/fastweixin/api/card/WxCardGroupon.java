package com.meiqi.liduoo.fastweixin.api.card;

import org.json.JSONException;

/**
 *
 * @author jackylian
 */
public class WxCardGroupon extends WxCard
{

    public WxCardGroupon() throws JSONException
    {
        init("GROUPON");
    }
    
    public void setDealDetail(String detail) throws JSONException
    {
        m_data.put("deal_detail", detail);
    }
    
}

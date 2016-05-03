package com.meiqi.liduoo.fastweixin.api.card;


import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;

/**
 *
 * @author jackylian
 */
public class WxCardBaseInfo
{

    org.json.JSONObject m_data;

    public WxCardBaseInfo() throws JSONException
    {
        m_data = new org.json.JSONObject();
        m_data.put("date_info", new org.json.JSONObject());
        m_data.put("location_id_list", new org.json.JSONArray());
        m_data.put("sku", new org.json.JSONObject());
    }

    public String toJsonString()
    {
        return m_data.toString();
    }

    public String toString()
    {
        return toJsonString();
    }

    public void setLogoUrl(String logoUrl) throws JSONException
    {
        m_data.put("logo_url", logoUrl);
    }

    public String getLogoUrl()
    {
        return m_data.optString("logo_url");
    }

    static int CODE_TYPE_TEXT = 0;
    static int CODE_TYPE_BARCODE = 1;
    static int CODE_TYPE_QRCODE = 2;

    public void setCodeType(int code) throws JSONException
    {
        m_data.put("code_type", code);
    }

    public int getCodeType()
    {
        return m_data.optInt("code_type");
    }

    public void setBrandName(String name) throws JSONException
    {
        m_data.put("brand_name", name);
    }

    public String GetBrandName()
    {
        return m_data.optString("brand_name");
    }

    public void setTitle(String title) throws JSONException
    {
        m_data.put("title", title);
    }

    public String getTitle()
    {
        return m_data.optString("title");
    }

    public void setSubTitle(String subTitle) throws JSONException
    {
        m_data.put("sub_title", subTitle);
    }

    public String getSubTitle()
    {
        return m_data.optString("sub_title");
    }

    public void setDateInfoTimeRange(Date beginTime, Date endTime) throws JSONException
    {
        setDateInfoTimeRange(beginTime.getTime() / 1000, endTime.getTime() / 1000);
    }

    public void setDateInfoTimeRange(long beginTimestamp, long endTimestamp) throws JSONException
    {
        getDateInfo().put("type", 1);
        getDateInfo().put("begin_timestamp", beginTimestamp);
        getDateInfo().put("end_timestamp", endTimestamp);
    }

    public void setDateInfoFixTerm(int fixedTerm) throws JSONException
    {
        setDateInfoFixTerm(fixedTerm, 0);
    }

    public void setDateInfoFixTerm(int fixedTerm, int fixedBeginTerm) throws JSONException //fixedBeginTerm是领取后多少天开始生效
    {
        getDateInfo().put("type", 2);
        getDateInfo().put("fixed_term", fixedTerm);
        getDateInfo().put("fixed_begin_term", fixedBeginTerm);
    }

    public org.json.JSONObject getDateInfo()
    {
        return m_data.optJSONObject("date_info");
    }

    public void setColor(String color) throws JSONException
    {
        m_data.put("color", color);
    }

    public String getColor()
    {
        return m_data.optString("color");
    }

    public void setNotice(String notice) throws JSONException
    {
        m_data.put("notice", notice);
    }

    public String getNotice()
    {
        return m_data.optString("notice");
    }

    public void setServicePhone(String phone) throws JSONException
    {
        m_data.put("service_phone", phone);
    }

    public String getServicePhone()
    {
        return m_data.optString("service_phone");
    }

    public void setDescription(String desc) throws JSONException
    {
        m_data.put("description", desc);
    }

    public String getDescription()
    {
        return m_data.optString("description");
    }

    public void setLocationIdList(Collection<Integer> value) throws JSONException
    {
        org.json.JSONArray array = new org.json.JSONArray();
//        value.stream().forEach((integer) ->
//        {
//            array.put(integer);
//        });
        Iterator<Integer> it = value.iterator();
        while(it.hasNext()) {
        	array.put(it.next());
        }
       
        m_data.put("location_id_list", array);
    }
    
    public void addLocationIdList(int locationId) throws JSONException
    {
        getLocationIdList().put(locationId);
    }
    
    public org.json.JSONArray getLocationIdList() throws JSONException
    {
        return m_data.getJSONArray("location_id_list");
    }

    public void setUseLimit(int limit) throws JSONException
    {
        m_data.put("use_limit", limit);
    }

    public int getUseLimit()
    {
        return m_data.optInt("useLimit");
    }

    public void setGetLimit(int limit) throws JSONException
    {
        m_data.put("get_limit", limit);
    }

    public int getGetLimit()
    {
        return m_data.optInt("get_limit");
    }

    public void setCanShare(boolean canShare) throws JSONException
    {
        m_data.put("can_share", canShare);
    }

    public boolean getCanShare()
    {
        return m_data.optBoolean("can_share");
    }

    public void setCanGiveFriend(boolean canGive) throws JSONException
    {
        m_data.put("can_give_friend", canGive);
    }

    public boolean getCanGiveFriend()
    {
        return m_data.optBoolean("can_give_friend");
    }

    public void setUseCustomCode(boolean isUse) throws JSONException
    {
        m_data.put("use_custom_code", isUse);
    }

    public boolean getUseCustomCode()
    {
        return m_data.optBoolean("use_custom_code");
    }

    public void setBindOpenid(boolean isBind) throws JSONException
    {
        m_data.put("bind_openid", isBind);
    }

    public boolean getBindOpenid()
    {
        return m_data.optBoolean("bind_openid");
    }
    
    public void setQuantity(int quantity) throws JSONException
    {
        m_data.optJSONObject("sku").put("quantity", quantity);
    }
   
    public int getQuantity()
    {
        return m_data.optJSONObject("sku").optInt("quantity");
    }
}

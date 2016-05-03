package com.meiqi.app.pojo;

/**
 * 广告相关统计(来源广告,来源位置,点击)
 * 
 * @ClassName: EcsAdsense
 * @Description:
 * @author 杨永川
 * @date 2015年4月7日 下午3:51:14
 *
 */
public class Adsense {
    private short  fromAd = 0;
    private String referer;
    private int    clicks = 0;



    public Adsense() {
    }



    public Adsense(short fromAd, String referer, int clicks) {
        super();
        this.fromAd = fromAd;
        this.referer = referer;
        this.clicks = clicks;
    }



    public short getFromAd() {
        return fromAd;
    }



    public void setFromAd(short fromAd) {
        this.fromAd = fromAd;
    }



    public String getReferer() {
        return referer;
    }



    public void setReferer(String referer) {
        this.referer = referer;
    }



    public int getClicks() {
        return clicks;
    }



    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

}
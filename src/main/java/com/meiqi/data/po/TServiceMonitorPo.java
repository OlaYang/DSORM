package com.meiqi.data.po;

/**
 * User: 
 * Date: 14-2-13
 * Time: 上午10:54
 */
public class TServiceMonitorPo {
    private Integer mid;
    private Long httptimes;
    private Integer cachesum;
    private String serviceinvoke;

    public Integer getCachesum() {
        return cachesum;
    }

    public void setCachesum(Integer cachesum) {
        this.cachesum = cachesum;
    }

    public Long getHttptimes() {
        return httptimes;
    }

    public void setHttptimes(Long httptimes) {
        this.httptimes = httptimes;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getServiceinvoke() {
        return serviceinvoke;
    }

    public void setServiceinvoke(String serviceinvoke) {
        this.serviceinvoke = serviceinvoke;
    }
}

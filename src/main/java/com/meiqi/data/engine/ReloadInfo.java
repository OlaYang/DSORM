package com.meiqi.data.engine;

import java.util.Map;

import com.meiqi.data.entity.TService;

/**
 * User: 
 * Date: 13-11-22
 * Time: 下午4:36
 */
public class ReloadInfo implements Comparable<ReloadInfo> {
    long lastAcTime;
    TService po;
    Map<String, Object> param;
    long latency;
    long createtime = 0;

    public ReloadInfo(long lastAcTime, TService po, Map<String, Object> param, long latency, long createtime) {
        this.lastAcTime = lastAcTime;
        this.po = po;
        this.param = param;
        this.latency = latency;
        this.createtime = createtime;
    }

    @Override
    public int compareTo(ReloadInfo o) {
        long tmp = this.lastAcTime - o.lastAcTime;

        if (tmp > 0) {
            return -1;
        } else if (tmp == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}

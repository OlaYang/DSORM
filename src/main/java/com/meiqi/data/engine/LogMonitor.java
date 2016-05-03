package com.meiqi.data.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.po.TServiceAlarmPo;
import com.meiqi.data.po.TServiceCallLogPo;
import com.meiqi.data.po.TServiceMonitorPo;
import com.meiqi.data.user.handler.service.ServiceReqInfo;
import com.meiqi.data.util.ConfigUtil;
import com.meiqi.data.util.LogUtil;

/**
 * User: 
 * Date: 13-10-18
 * Time: 下午3:08
 */
public class LogMonitor {
    //    private static Logger logMonitor = Logger.getLogger("monitor");
    //    private static Logger logalarm = Logger.getLogger("alarm");
    private static final int waiting_threshold = ConfigUtil.getWaiting_threshold();
    private static final ConcurrentLinkedQueue<TServiceAlarmPo> queue
            = new ConcurrentLinkedQueue<TServiceAlarmPo>();


    private static final ConcurrentLinkedQueue<TServiceCallLogPo> callQueue
            = new ConcurrentLinkedQueue<TServiceCallLogPo>();

    static class ServiceInvoke implements Comparable {
        public String key;
        public long time;

        ServiceInvoke(String key, long time) {
            this.key = key;
            this.time = time;
        }


        @Override
        public int compareTo(Object o) {
            return (int) (((ServiceInvoke) o).time - time);
        }
    }

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("rengine monitor");

                int times = 600;
                while (true) {
                    try {

                        if (times >= 600) {
                            times = 0;

                            TServiceMonitorPo po = new TServiceMonitorPo();

                            final ConcurrentHashMap<String, AtomicLong> serviceTimes = DataUtil.serviceTimes;
                            final List<ServiceInvoke> nodes = new ArrayList<ServiceInvoke>();

                            for (Map.Entry<String, AtomicLong> entry : serviceTimes.entrySet()) {
                                nodes.add(new ServiceInvoke(entry.getKey(), entry.getValue().get()));
                            }

                            Collections.sort(nodes);

                            int count = 0;
                            for (Map.Entry<String, ConcurrentHashMap<String, D2Data>> entry
                                    : Cache4BaseService.DATA_CACHE.entrySet()) {
                                final int size = entry.getValue().size();
                                count += size;
                            }
                            po.setCachesum(count);
                            po.setServiceinvoke(JSON.toJSONString(nodes));

                            try {
//                                Services.client.insert("insertTServiceMonitor", po);
                            } catch (Exception e) {
                                //
                            }
                        }

                        TServiceAlarmPo err;
                        while ((err = queue.poll()) != null) {
                            try {
//                                Services.client.insert("insertTServiceAlarm", err);
                            } catch (Exception e) {
                                //
                            }
                        }

                        TServiceCallLogPo callLogPo;
                        while ((callLogPo = callQueue.poll()) != null) {
                            try {
                                ServiceReqInfo serviceReqInfo = JSON.parseObject(callLogPo.getParam(),
                                        ServiceReqInfo.class);
                                callLogPo.setServiceName(serviceReqInfo.getServiceName());
                                callLogPo.setParam(JSON.toJSONString(serviceReqInfo.getParam()));
                                callLogPo.setTime(new Date());
//                                Services.client.insert("insertTServiceCallLog", callLogPo);  // 测试环境新增的程序方调用记录
                            } catch (Exception e) {
                                //
                            }
                        }
                        
                    } catch (Exception e) {
                        //
                    } finally {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            times++;
                        } catch (Exception e) {
                            //
                        }
                    }
                }
            }
        }).start();
    }

    public static void start() {
    }

    public static void error(String sname, String msg) {
        queue.offer(new TServiceAlarmPo(sname, LogUtil.str2OneLine(msg)));
    }


    public static void callLog(String ip, String content) {
        TServiceCallLogPo callLogPo = new TServiceCallLogPo();
        callLogPo.setIp(ip);
        callLogPo.setParam(content);
        callQueue.offer(callLogPo);
    }
}

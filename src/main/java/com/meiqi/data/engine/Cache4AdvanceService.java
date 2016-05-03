package com.meiqi.data.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.ExcelEngineTool;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.util.LogUtil;

/**
 * User: 
 * Date: 13-8-16
 * Time: 上午10:35
 */
public final class Cache4AdvanceService {

    /**
     * 全局高级数据源缓存, 缓存层次是: 数据源名称-参数-d2data
     */
    static final ConcurrentHashMap<String, ConcurrentHashMap<String, D2Data>> DATA_CACHE
            = new ConcurrentHashMap<String, ConcurrentHashMap<String, D2Data>>();

    /**
     * 高级数据源的计算, 优先计算基本数据源, 然后在计算高级数据源
     *
     * @param servicePo
     * @param param
     * @param callLayer
     * @return
     * @throws RengineException
     */
    static D2Data getD2Data(TService servicePo, Map<String, Object> param, int callLayer) throws RengineException {
        if (callLayer > 30) {
            throw new RengineException(servicePo.getName(), "数据源嵌套调用层次不得深于30");
        }

        if (servicePo.getColumns().size() == 0) {
            throw new RengineException(servicePo.getName(), "列数目为0, ");
        }

        final String serviceName = servicePo.getName();
        // llcheng start
        final long start = System.currentTimeMillis();
        final long currentSecond = start / 1000;
        final int cacheTime = servicePo.getCacheTime() == null ? 0 : servicePo.getCacheTime();
        final long updateTimestamp = servicePo.getUpdateTime() == null ? 0L : servicePo.getUpdateTime().getTime();
        String key = JSON.toJSONString(param);

        D2Data cacheData = checkAdvanceGlobalCache(serviceName, cacheTime, key, updateTimestamp, start, currentSecond);
        if (cacheData == null) {
            // llcheng end
            final Integer baseServiceID = servicePo.getBaseServiceID();
            final String baseServicename = Services.id2Name(baseServiceID);
            final TService baseServicePo = Services.getService(baseServicename);
            if (baseServicename == null || baseServicePo == null) {
                throw new RengineException(serviceName, "基本数据源ID: " + baseServiceID + " 未找到");
            }

            CalInfo calInfo = new CalInfo(callLayer, param, servicePo);
            try {
                final D2Data baseD2Data =
                        Cache4D2Data.getD2Data(baseServicePo, param, callLayer, servicePo, param, "父级调用");
                final D2Data d2Data = cal(baseD2Data, calInfo, servicePo);
                calInfo = null;
                long latency = System.currentTimeMillis() - start; // llcheng
                if (LogUtil.isDebugEnabled()) {
                    LogUtil.debug("rows " + d2Data.getData().length + ", columns: " + d2Data.getColumnList().size());
                }

                saveAdvanceGlobalCache(serviceName, cacheTime, key, d2Data, latency, currentSecond, param, updateTimestamp, start);// llcheng

                return d2Data;
            } catch (RengineException e) {
                LogUtil.error("Cache4AdvanceService getD2Data error:"+e.getMessage());
                e.addInvoke(calInfo);
                throw e;
            }
            // llcheng start
        } else {
            // LogUtil.info(serviceName + " 命中高级全局缓存");
            return cacheData;
        }
        // llcheng end
    }

    /**
     * 创建新的d2data, 配置新的二维数据和列信息
     * 计算完后, 返回
     *
     * @param baseD2Data
     * @param calInfo
     * @param servicePo
     * @return
     * @throws RengineException
     */
    private static D2Data cal(D2Data baseD2Data, CalInfo calInfo, TService servicePo)
            throws RengineException {
        Object[][] basedata = baseD2Data.getData();
        Object[][] data = basedata;
        final int maxRow = basedata.length;

        final List<TServiceColumn> columnList = new ArrayList<TServiceColumn>(
                baseD2Data.getColumnList().size() + servicePo.getColumns().size());
        final D2Data d2Data = new D2Data(columnList);
        d2Data.setData(data);

        for (TServiceColumn column : baseD2Data.getColumnList()) {
            columnList.add(column);
        }
        for (TServiceColumn column : servicePo.getColumns()) {
            columnList.add(column);
        }

        if (maxRow != 0) {  // 有数据，进行COPY和扩容
            int maxColumnIndex = -1;
            for (TServiceColumn column : columnList) {
                final int index;
                index = column.getColumnIntIndex();
                maxColumnIndex = index > maxColumnIndex ? index : maxColumnIndex;
            }

            int needColumnSize = maxColumnIndex + 1; // 实际需要的列数目

            data = new Object[maxRow][];
            Object[] rowData;
            long start = System.currentTimeMillis();
            for (int i = 0; i < maxRow; i++) {
                rowData = new Object[needColumnSize];
                System.arraycopy(basedata[i], 0, rowData, 0, basedata[i].length); // 复制原有数据, 原有数据可能被多个高级数据源使用
                data[i] = rowData;
            }
            long spendTime = System.currentTimeMillis() - start;
            LogUtil.info(Thread.currentThread().getName() + " arraycopy time" + spendTime);
            d2Data.setData(data);

            calInfo.setCurD2data(d2Data);
            calInfo.setMaxRow(maxRow);

            ExcelEngineTool.process(d2Data, calInfo);
        }

        return d2Data;
    }

    /**
     * 检测高级规则全局缓存
     *
     * @param serviceName
     * @param cacheTime
     * @param key
     * @return
     * @throws RengineException
     */
    static D2Data checkAdvanceGlobalCache(String serviceName, int cacheTime, String key, long updateTimestamp, long start, long currentSecond) throws RengineException {


        ConcurrentHashMap<String, D2Data> cache = null;
        if (cacheTime > 0 && serviceName != null) {
            cache = DATA_CACHE.get(serviceName);
            if (cache == null) {
                cache = new ConcurrentHashMap<String, D2Data>();
                final ConcurrentHashMap<String, D2Data> oldCache = DATA_CACHE.putIfAbsent(serviceName, cache);
                if (oldCache != null) {
                    cache = oldCache;
                }
            }
            D2Data data = cache.get(key);

            if (data != null
                    && (currentSecond - data.createtime <= cacheTime)
                    && data.timestamp == updateTimestamp) {  // cache hit
                data.lastAcTime = start;
                return data;
            } else {
                // cache miss
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * 存储高级规则全局缓存
     *
     * @param serviceName
     * @param cacheTime
     * @param key
     * @return
     * @throws RengineException
     */
    static void saveAdvanceGlobalCache(String serviceName, int cacheTime, String key, D2Data data, long latency, long currentSecond, Map<String, Object> param, long timestamp, long start) throws RengineException {

        ConcurrentHashMap<String, D2Data> cache = null;
        if (cacheTime > 0 && serviceName != null) {
            cache = DATA_CACHE.get(serviceName);
            if (cache == null) {
                cache = new ConcurrentHashMap<String, D2Data>();
                final ConcurrentHashMap<String, D2Data> oldCache = DATA_CACHE.putIfAbsent(serviceName, cache);
                if (oldCache != null) {
                    cache = oldCache;
                }
            }

//            if (cacheTime >= Services.RELOAD_CACHETIME_THRESHOLD
//                    && latency <= Services.RELOAD_LATENCY_THRESHOLD) {
//                data.setProcessInfo(currentSecond, latency, param, serviceName, timestamp, start);
//            }
            data.setProcessInfo(currentSecond, latency, param, serviceName, timestamp, start);
            cache.put(key, data);

        }
    }


}

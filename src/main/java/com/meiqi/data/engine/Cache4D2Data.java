package com.meiqi.data.engine;

import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.util.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: 
 * Date: 13-8-16
 * Time: 上午10:34
 */
public final class Cache4D2Data {
    private static final ThreadLocal<Map<String, Map<Map<String, Object>, D2Data>>> cache
            = new ThreadLocal<Map<String, Map<Map<String, Object>, D2Data>>>() {
        @Override
        protected Map<String, Map<Map<String, Object>, D2Data>> initialValue() {
            return new HashMap<String, Map<Map<String, Object>, D2Data>>();
        }
    };

    private static final boolean tlsEnabled = ConfigUtil.getTlsEnabled();
    private static final D2Data EMPTY = new D2Data(new ArrayList<TServiceColumn>(0));

    /**
     * 可递归的计算入口, 会依据father判断是否构成环调用
     * att代表切入这个计算的参考信息
     * 函数在检测完环后, 根据数据源类型调用高级和基本类进行计算, 对结果依据配置参数进行缓存
     *
     * @param servicePo
     * @param param
     * @param callLayer
     * @param father
     * @param fatherParam
     * @param att
     * @return
     * @throws RengineException
     */
    public static D2Data getD2Data(TService servicePo, Map<String, Object> param, int callLayer
            , TService father, Map<String, Object> fatherParam, String att)
            throws RengineException {
        if (servicePo == null || param == null) {
            throw new RengineException(null, "数据源配置信息 或者 参数 为null");
        }
        
        if(fatherParam!=null && fatherParam.get("site_id")!=null){
            param.put("site_id", fatherParam.get("site_id"));
        }

        InvokeNode node = ProcessInfos.addLink(father, fatherParam, servicePo, param, att);

        final Map<String, Map<Map<String, Object>, D2Data>> cacheIN = cache.get();
        final String serviceName = servicePo.getName();

        Map<Map<String, Object>, D2Data> cacheInService = cacheIN.get(serviceName);

        if (cacheInService == null) {
            cacheInService = new HashMap<Map<String, Object>, D2Data>();
            cacheIN.put(serviceName, cacheInService);
        }

        // StackTrace.printStackTrace();
        Thread.currentThread().setName(serviceName+"-"+Thread.currentThread().getId());


        D2Data d2Data = cacheInService.get(param);
        if (d2Data == null || !tlsEnabled) {
            long time = System.currentTimeMillis();
            if (servicePo.getType().equalsIgnoreCase("base")) {
                d2Data = Cache4BaseService.getD2Data(servicePo, param);
            } else if(servicePo.getType().equalsIgnoreCase("wx")){
            	d2Data = Cache4WXService.getD2Data(servicePo, param, callLayer+1);
            } else if(servicePo.getType().equalsIgnoreCase("weixin")){
            	d2Data = CacheWeiXinBaseService.getWeiXinDate(servicePo, param);
            } else {
                d2Data = Cache4AdvanceService.getD2Data(servicePo, param, callLayer + 1);
            }

            if (node != null) {
                node.setTime(System.currentTimeMillis() - time);
                node.setLines(d2Data.getData().length);
            }

            if (tlsEnabled) { // 启用时存入
                cacheInService.put(param, d2Data);
            } else { // 未启用时仅存入占位符
                cacheInService.put(param, EMPTY);
            }
        }

        return d2Data;
    }

    /**
     * 获取对应参数的计算结果, 用于判断是不是有环调用
     *
     * @param serviceName
     * @param param
     * @return
     */
    static D2Data peek(String serviceName, Map<String, Object> param) {
        final Map<String, Map<Map<String, Object>, D2Data>> cacheIN = cache.get();
        Map<Map<String, Object>, D2Data> cacheInService = cacheIN.get(serviceName);

        if (cacheInService != null) {
            D2Data d2Data = cacheInService.get(param);
            return d2Data;
        }
        return null;
    }

    static void clear() {
        cache.get().clear();
    }
}

package com.meiqi.openservice.commons.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.data.engine.RengineException;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;

@Component
public class RuleExceptionUtil {
    @Autowired
    private IMushroomAction     mushroomAction;

    @Autowired
    private IMemcacheAction     memcacheService;

    @Autowired
    private ThreadHelper        indexTheadHelper;

    // 规则异常信息
    private static final String MKEY_RULE_EXCEPTION = MD5Util.MD5("lejj_rule_exception");

    private RengineException    re                  = null;



    public RuleExceptionUtil() {

    }



    public RuleExceptionUtil(Exception e) {
        if (e instanceof RengineException) {
            re = (RengineException) e;
        }
    }



    public void run(Exception e) {
        if (e instanceof RengineException) {
            re = (RengineException) e;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (re != null && !StringUtils.isEmpty(re.getServiceName())) {
                        // mushroomAction.insertRuleException2DB(re.getServiceName(),
                        // re.getMessage());
                        // long time = System.currentTimeMillis();
                        ExceptionMemcachedThread thread = new ExceptionMemcachedThread();
                        indexTheadHelper.execute(thread);
                        // System.out.println("规则异常写入耗时：" +
                        // (System.currentTimeMillis() - time));
                    }

                }
            }).start();
        }
    }

    class ExceptionMemcachedThread implements ThreadCallback {

        @SuppressWarnings("unchecked")
        public void run() {
            // mushroomAction.insertRuleException2DB(re.getServiceName(),
            // re.getMessage());
            LogUtil.info("开始写入数据源错误信息！");
            String key = re.getServiceName();// + "_" + DateUtils.getTime();
            Object cacheObj = memcacheService.getCache(MKEY_RULE_EXCEPTION);
            Map<String, Map<String, Object>> cacheMap = null;
            if (null != cacheObj) {
                cacheMap = DataUtil.parse(cacheObj.toString(), Map.class);// 从缓存中获取对应值
            } else {
                cacheMap = new HashMap<String, Map<String, Object>>();
            }
            Map<String, Object> keyMap = new HashMap<String, Object>();
            boolean flag = false;
            if (cacheMap.containsKey(key)) {
                Map<String, Object> objMap = cacheMap.get(key);
                if (null != objMap && !objMap.get("msg").equals(re.getMessage())) {
                    keyMap.put("sname", re.getServiceName());
                    keyMap.put("msg", re.getMessage());
                    cacheMap.put(key + "_" + DateUtils.getTime(), keyMap);
                    flag = memcacheService.putCache(MKEY_RULE_EXCEPTION, DataUtil.toJSONString(cacheMap));
                    if(flag){
                        LogUtil.info("数据源不同的异常信息,存入缓存成功！");
                    }else{
                        LogUtil.info("数据源不同的异常信息,存入缓存失败！");
                    }
                }
            } else {
                keyMap.put("sname", re.getServiceName());
                keyMap.put("msg", re.getMessage());
                cacheMap.put(key, keyMap);
                flag = memcacheService.putCache(MKEY_RULE_EXCEPTION, DataUtil.toJSONString(cacheMap));
                if(flag){
                    LogUtil.info("数据源不同的异常信息,存入缓存成功！");
                }else{
                    LogUtil.info("数据源不同的异常信息,存入缓存失败！");
                }
            }
            
        }
    }
}

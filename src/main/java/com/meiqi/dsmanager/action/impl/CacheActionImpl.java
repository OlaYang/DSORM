package com.meiqi.dsmanager.action.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.dsmanager.action.ICacheAction;
import com.meiqi.dsmanager.action.IDataMappingAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.common.config.CacheLevelConfig;
import com.meiqi.dsmanager.common.config.ConcurrentLinkedHashMapConfig;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.common.config.MemcacheConfig;
import com.meiqi.dsmanager.entity.DataMapping;
import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.CacheUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;

@Service
public class CacheActionImpl implements ICacheAction {

    @Autowired
    private IMemcacheAction    memcacheService;

    @Autowired
    private IDataMappingAction dataMappingService;

    @Autowired
    private ThreadHelper indexTheadHelper;

    /*
     * Title: preCache Description:
     * 
     * @param content
     * 
     * @return
     * 
     * @see com.meiqi.dsmanager.action.ICacheAction#preCache(java.lang.String)
     */
    @Override
    public boolean preCache(DsManageReqInfo reqInfo, DataSources dataSource, ResponseBaseData responseData) {
        String memKey = memcacheService.createCacheKey(JSON.toJSONString(reqInfo));// 计算出缓存key
        boolean isGetForData = true; // 标记是否需要从规则引擎中取数据 true=从data取值 false=get
        try {
            // 检查是否该请求数据源是否执行dsmanger缓存.存在则执行dsmang缓存
            if (CacheLevelConfig.DSMANAGER_CACHE.level == dataSource.getCacheLevel()) {
                // 从缓存状态表中获取dsname下更新了的表
                List<DataMapping> dataMappingList = dataMappingService.findDataMappingByDsNameAndMappingStatus(
                        reqInfo.getServiceName(), false);
                // 如果有dsname中有需要更新的表，那么从数据库中取值并更新缓存
                if (dataMappingList != null && !dataMappingList.isEmpty()) {
                    dataMappingService.removeMappingMap(reqInfo.getServiceName()); // 发生修改，清楚所有的数据
                    dataMappingService.updateMappingToTrueStatusByDSName(dataMappingList);
                    // 如果没有需要更新的表，那么从ds缓存中取值
                } else {
                    // 从缓存中取出值
                    responseData = (ResponseBaseData) CacheUtil.getCache(memKey);
                    // 检查是否取出有值，没有到数据库中取
                    if (null == responseData) {
                        isGetForData = true;// 修改状态为数据需要从规则引擎获取
                    } else {
                        isGetForData = false; // 标记已经从缓存中获取了
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            responseData.setCode(DsResponseCodeData.ERROR.code);
            responseData.setDescription(DsResponseCodeData.ERROR.description);
        }
        return isGetForData;
    }



    /*
     * Title: postCache Description:异步刷新mamcached缓存
     * 
     * @param content
     * 
     * @see com.meiqi.dsmanager.action.ICacheAction#postCache(java.lang.String)
     */
    @Override
    public void postCache(DsManageReqInfo reqInfo, ResponseBaseData responseData, DataSources dataSource,
            String keyContent,String formatString) {
        cacheThread thread=new cacheThread(reqInfo, responseData, dataSource,keyContent,formatString);
        indexTheadHelper.execute(thread);
    }
    
    class cacheThread implements ThreadCallback {
        
        private DsManageReqInfo reqInfo;
        private ResponseBaseData responseData;
        private DataSources dataSource;
        private String keyContent;
        private String formatString;
        
        public cacheThread(DsManageReqInfo reqInfo, 
                ResponseBaseData responseData, 
                DataSources dataSource,
                String keyContent,String 
                formatString){
            this.reqInfo=reqInfo;
            this.responseData=responseData;
            this.dataSource=dataSource;
            this.keyContent=keyContent;
            this.formatString=formatString;
        }
        @Override
        public void run() {
            if (!"".equals(keyContent.trim())) {
                if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
                    String memKey = memcacheService.createCacheKey(keyContent);// 计算出缓存key
                    // 接收数据成功才去做缓存,同时需要缓存等级为memcache才进行memcache缓存
                    //LogUtil.info("cachelevel "+reqInfo.getServiceName()+" reqJson:" + keyContent + ",cachelevel:" + dataSource.getCacheLevel());
                    if (CacheLevelConfig.MEMCACHE_CACHE.level == dataSource.getCacheLevel()) {
                        if (null != reqInfo.getServiceName()) {
                            // 从memecache中取取serviceName，取出的一个list
                            String serviceNameKey = memcacheService.createCacheKey(reqInfo.getServiceName());// 计算serviceName的key
                            @SuppressWarnings("unchecked")
                            Set<String> memMappingSet=null;
                            try {
                                memMappingSet = (Set<String>) memcacheService.getCache(serviceNameKey);
                            } catch (Exception e) {
                                LogUtil.error("serviceName:"+reqInfo.getServiceName()+",serviceNameKey:"+serviceNameKey+",对应的memcached缓存值类型不是set,error:"+e.getMessage());
                            }
                            if (null == memMappingSet) {// 为空则是新增
                                // 新建集合
                                memMappingSet = new HashSet<String>();
                            }
                            // 把生成的key加入集合
                            memMappingSet.add(memKey);
                            // 存入memcache
                            long begin=System.currentTimeMillis();
                            boolean result1 = memcacheService.putCache(serviceNameKey, memMappingSet);
                            long time1=System.currentTimeMillis()-begin;
                            if(time1>4){
                                LogUtil.info("add memcache time long"+reqInfo.getServiceName()+" reqJson:" + keyContent +",result1="+result1+",time1="+time1+ ",serviceNameKey=" + serviceNameKey);
                            }
                            long time11=System.currentTimeMillis();
                            boolean result2 = memcacheService.putCache(memKey, formatString,
                                    MemcacheConfig.CACHE_TIME_TEN_MINUTE.time);
                            long time2=System.currentTimeMillis()-time11;
                            if(time2>4){
                                LogUtil.info("add memcache time long"+reqInfo.getServiceName()+" reqJson:" + keyContent +",result2="+result2+",time2="+time2+ ",memKey=" + memKey);
                                
                            }
                            LogUtil.info("add memcache "+reqInfo.getServiceName()+" reqJson:" + keyContent + ",memKey:" + memKey +",result1="+result1+",time1="+time1+ ",serviceNameKey=" + serviceNameKey+",result2="+result2+",time2="+time2);
                        }
                        // 设置缓存,缓存十分钟

                    } else if (CacheLevelConfig.DSMANAGER_CACHE.level == dataSource.getCacheLevel()) {
                        CacheUtil.putCache(memKey, formatString,
                                ConcurrentLinkedHashMapConfig.CACHE_NOT_STALE.time);
                        dataMappingService.addOneInMappingMap(reqInfo.getServiceName(), memKey); // 将dsname与key加入到map关系缓存中
                    }
                }
            }
        }
    }
}

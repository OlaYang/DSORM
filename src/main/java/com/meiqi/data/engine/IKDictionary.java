package com.meiqi.data.engine;


import com.alibaba.fastjson.JSON;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.user.handler.service.ServiceRespInfo;
import com.meiqi.data.util.HttpClientUtil;
import com.meiqi.data.util.LogUtil;
import com.lejj.crow.client.CrowClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-7-4
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public final class IKDictionary {
    private static Set<String> ikDictionary;
    private static final String ikService = "分词字典";
    private static Map<String, Object> param = new ConcurrentHashMap<String, Object>();
    private static CrowClient CROW_CLIENT;
    private static final String _solr_keywords = "MLL_BUV1_GoodsKeyword";
    private static final String _solr_goodsCat = "MLL_BUV1_GoodsCat";
    private static List<Map<String, String>> solrKeywords;
    private static List<Map<String, String>> solrGoodsCats;

    static {
        ikDictionary = new CopyOnWriteArraySet<String>();
        loadDictionary();
        loadSolrKeywords();
    }

    public static void loadDictionary() {

        try {
            ikDictionary.clear();
            TService po = Services.getService(ikService);
            param.clear();
            param.put("num", "1");
            D2Data d2Data = DataUtil.getD2Data(po, param);
            for (int i = 0; i < d2Data.getData().length; i++) {
                Object word = d2Data.getValue("词汇", i);
                ikDictionary.add(String.valueOf(word));
            }
        } catch (Exception e) {
            // 加载词典失败
            LogUtil.error("词典加载失败：" + e.getMessage());
        }
    }

    // 获取第三方分词字典
    public static Set<String> getDictionary() {
        return ikDictionary;
    }

    //加载第三方搜索引擎关键词库及分类词库
    public static void loadSolrKeywords() {
        param.clear();
        param.put("dbLang", "zh");
        try {
            param.put("serviceName", _solr_keywords);
            ServiceRespInfo serviceRespInfo = JSON.parseObject(HttpClientUtil.postRuleService(JSON.toJSONString(param)), ServiceRespInfo.class);
            solrKeywords = serviceRespInfo.getRows();
        } catch (Exception e) {
            LogUtil.error("solr关键词库加载失败：" + e.getMessage());
            //
        }

        try {
            param.put("serviceName", _solr_goodsCat);
            ServiceRespInfo serviceRespInfo = JSON.parseObject(HttpClientUtil.postRuleService(JSON.toJSONString(param)), ServiceRespInfo.class);
            solrGoodsCats = serviceRespInfo.getRows();
        } catch (Exception e) {
            LogUtil.error("solr分类词库加载失败：" + e.getMessage());
            //
        }
    }

    // 获取第三方搜索引擎词库
    public static List<Map<String, String>> getSolrKeywords() {
        return solrKeywords;
    }

    // 获取第三方搜索引擎分类词库
    public static List<Map<String, String>> getSolrGoodsCats() {
        return solrGoodsCats;
    }

}

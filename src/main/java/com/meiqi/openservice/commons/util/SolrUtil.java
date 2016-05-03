package com.meiqi.openservice.commons.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.HttpUtil;
import com.meiqi.openservice.commons.config.SysConfig;

/**
 * 
 * solr工具类
 *
 * @author: 杨永川
 * @version: 1.0, 2015年8月24日
 */
public class SolrUtil {
    private static final Logger LOG        = Logger.getLogger(SolrUtil.class);
    private static final String URL_ENCODE = "utf-8";



    /**
     * 
     * 获取中文分词结果
     *
     * @param schemaName
     * @param wt
     * @param fieldValue
     * @param fieldtype
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public static String getChinaWordsResults(Map<String, Object> param) {
        LOG.info("Function:getChinaWordsResults.Start.");
        String schemaName = (String) param.get("schemaName");
        String fieldValue = (String) param.get("fieldValue");
        String fieldType = (String) param.get("fieldType");
        String wt = "json";
        String fieldtype = "string";

        String solrServerUrl = SysConfig.getValue("solr.solrServerUrl");
        // 测试
        // String solrServerUrl = "http://solr.lejj.com:8181/solr";
        StringBuffer urlStringBuffer = new StringBuffer();
        try {
            urlStringBuffer.append(solrServerUrl).append("/").append(schemaName).append("/analysis/field")
                    .append("?wt=").append(wt).append("&analysis.showmatch=true").append("&analysis.fieldvalue=")
                    .append(URLEncoder.encode(fieldValue, URL_ENCODE)).append("&analysis.query=")
                    .append(URLEncoder.encode(fieldValue, URL_ENCODE)).append("&analysis.fieldtype=").append(fieldtype);
        } catch (UnsupportedEncodingException e) {
            LOG.info("SolrUtil exception:" + e.getMessage());
        }

        LOG.info("获取中文分词结果，url:" + urlStringBuffer.toString());
        String result = HttpUtil.getHtmlContent(urlStringBuffer.toString());
        result = formatJson(result, fieldType);
        LOG.info("Function:getChinaWordsResults.End.");
        return result;
    }



    /**
     * 
     * 格式化结果
     *
     * @param resultData
     * @param fieldType
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String formatJson(String resultData, String fieldType) {
        if (StringUtils.isEmpty(resultData)) {
            return resultData;
        }
        if (StringUtils.isEmpty(fieldType)) {
            fieldType = "string";
        }
        // 返回结果
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> indexResult = new ArrayList<String>();
        List<String> queryResult = new ArrayList<String>();
        Map<String, Object> resultMap = DataUtil.parse(resultData, Map.class);
        Map analysis = (Map) resultMap.get("analysis");
        Map fieldTypesMap = (Map) analysis.get("field_types");

        Map stringMap = (Map) fieldTypesMap.get("string");
        List<Map<String, Object>> indexList = (List<Map<String, Object>>) stringMap.get("index");
        if (!CollectionsUtils.isNull(indexList) && indexList.size() > 1) {
            List<Map<String, Object>> index1 = (List<Map<String, Object>>) indexList.get(1);
            for (Map<String, Object> map : index1) {
                String text = (String) map.get("text");
                if (StringUtils.isNotEmpty(text)) {
                    indexResult.add(text);
                }
            }
        }

        List<Map<String, Object>> queryList = (List<Map<String, Object>>) stringMap.get("query");
        if (!CollectionsUtils.isNull(indexList) && indexList.size() > 1) {
            List<Map<String, Object>> query1 = (List<Map<String, Object>>) queryList.get(1);
            for (Map<String, Object> map : query1) {
                String text = (String) map.get("text");
                if (StringUtils.isNotEmpty(text)) {
                    queryResult.add(text);
                }
            }
        }

        result.put("index", indexResult);
        result.put("query", queryResult);
        return DataUtil.toJSONString(result);
    }

    public void testName() throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("schemaName", "app_goods_solr");
        param.put("fieldValue", "现代床");
        param.put("fieldType", "string");
        String result = getChinaWordsResults(param);
        System.err.println(result);
    }
}

package com.meiqi.app.common.utils.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.MallAdvertisement;
import com.meilele.datalayer.common.data.CommandManager;
import com.meilele.datalayer.common.data.ResultReader;
import com.meilele.datalayer.common.data.SearchCommand;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;

/**
 * 
 * @ClassName: RuleSearchUtil
 * @Description:规则引擎查询相关工具方法
 * @author 杨永川
 * @date 2015年5月15日 下午8:47:16
 *
 */
public final class RuleSearchUtil {

    private static final Logger LOGGER = Logger.getLogger(RuleSearchUtil.class);



    public static ResultReader getInfoByRuleName(String ruleName, String paramName, Object paramValue) {
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        searchCommand.setParam(paramName, paramValue);
        return excuteSearchCommand(searchCommand);
    }



    public static ResultReader getInfoByRuleName(String ruleName, Map<String, Object> paramsMap) {
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        for (Entry<String, Object> param : paramsMap.entrySet()) {
            searchCommand.setParam(param.getKey(), param.getValue());
        }
        return excuteSearchCommand(searchCommand);
    }



    private static ResultReader excuteSearchCommand(SearchCommand searchCommand) {
        try {
            return searchCommand.executeReader();

        } catch (Exception e) {
            LOGGER.error(searchCommand.getActionName() + " search error! " + e.getMessage(), e);
        }
        return null;
    }



    /**
     * 
     * @Title: getInfoByRuleName
     * @Description:调用规则引擎，有参数
     * @param @param ruleName
     * @param @param paramsMap
     * @param @param cls
     * @param @return
     * @return T
     * @throws
     */
    public static <T> T getInfoByRuleName(String ruleName, Map<String, Object> paramsMap, Class<T> cls) {
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        for (Entry<String, Object> param : paramsMap.entrySet()) {
            searchCommand.setParam(param.getKey(), param.getValue());
        }
        try {
            return (T) searchCommand.executeBean(cls);

        } catch (Exception e) {
            LOGGER.error(searchCommand.getActionName() + " search error! " + e.getMessage(), e);
        }
        return null;

    }



    /**
     * 
     * @Title: getInfoByRuleName
     * @Description:调用规则引擎，没有参数
     * @param @param ruleName
     * @param @param cls
     * @param @return
     * @return T
     * @throws
     */
    public static <T> T getInfoByRuleName(String ruleName, Class<T> cls) {
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        try {
            return (T) searchCommand.executeBean(cls);
        } catch (Exception e) {
            LOGGER.error(searchCommand.getActionName() + " search error! " + e.getMessage(), e);
        }
        return null;

    }



    /**
     * 
     * @Title: getInfoByRuleName
     * @Description:调用规则引擎，没有参数
     * @param @param ruleName
     * @param @param cls
     * @param @return
     * @return T
     * @throws
     */
    public static <T> List<T> getInfoListByRuleName(String ruleName, Class<T> cls) {
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        try {
            return (List<T>) searchCommand.executeBeanList(cls);
        } catch (Exception e) {
            LOGGER.error(searchCommand.getActionName() + " search error! " + e.getMessage(), e);
        }
        return null;

    }



    /**
     * 
     * @Title: getInfoListByRuleName
     * @Description:调用规则引擎，有参数
     * @param @param ruleName
     * @param @param paramsMap
     * @param @param cls
     * @param @return
     * @return List<T>
     * @throws
     */
    public static <T> List<T> getInfoListByRuleName(String ruleName, Map<String, Object> paramsMap, Class<T> cls) {
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        if (CollectionsUtils.isNull(paramsMap)) {
            return getInfoListByRuleName(ruleName, cls);
        }
        for (Entry<String, Object> param : paramsMap.entrySet()) {
            searchCommand.setParam(param.getKey(), param.getValue());
        }
        try {
            // ResultReader resultReader = searchCommand.executeReader();
            return (List<T>) searchCommand.executeBeanList(cls);
        } catch (Exception e) {
            LOGGER.error(searchCommand.getActionName() + " search error! " + e.getMessage(), e);
        }
        return null;

    }



    /**
     * 
     * @Title: getInfoListByRuleName
     * @Description:调用规则引擎，有参数 从指定参数获取数据
     * @param @param ruleName
     * @param @param paramsMap
     * @param @param cls
     * @param @param getParamName
     * @param @return
     * @return List<T>
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getInfoListByRuleName(String ruleName, Map<String, Object> paramsMap, Class<T> cls,
            String getParamName) {
        if (StringUtils.isBlank(getParamName)) {
            return (List<T>) getInfoListByRuleName(ruleName, paramsMap, Goods.class);
        }
        SearchCommand searchCommand = CommandManager.getSearchCommand(ruleName);
        if (CollectionsUtils.isNull(paramsMap)) {
            return getInfoListByRuleName(ruleName, cls);
        }
        for (Entry<String, Object> param : paramsMap.entrySet()) {
            searchCommand.setParam(param.getKey(), param.getValue());
        }
        try {
            // 返回参数 设置为map
            Map<String, Object> map = searchCommand.executeMap();
            if (map.containsKey(getParamName)) {
                List beanList = new LinkedList();
                // 获取指定属性 转换json array
                JSONArray json = JSONArray.fromObject(map.get(getParamName));
                List<Map<String, Object>> mapListJson = (List) json;
                for (int i = 0; i < mapListJson.size(); i++) {
                    Map<String, Object> obj = mapListJson.get(i);
                    // map 转换 object
                    beanList.add(BeanBuilder.buildBean(cls.newInstance(), obj));
                }
                return beanList;
            }

        } catch (Exception e) {
            LOGGER.error(searchCommand.getActionName() + " search error! " + e.getMessage(), e);
        }
        return null;

    }



    /**
     * 广告位查询
     * 
     * @param adCode
     * @return <pre>
     * String 广告位描述 = ResultReader.getString(&quot;adDesc&quot;);
     *                                                  String 广告位链接 = ResultReader.getString(&quot;adUrl&quot;);
     * </pre>
     */
    public static ResultReader adInfoSearch(String adCode) {
        return getInfoByRuleName("searchAdDesc", "adCode", adCode);
    }



    public static MallAdvertisement getHomeAd() {
        MallAdvertisement homeAd = null;
        SearchCommand searchCommand = CommandManager.getSearchCommand("getHomeAd");
        try {
            homeAd = searchCommand.executeBean(MallAdvertisement.class);
            // List<Ad> homeAdList = searchCommand.executeBeanList(Ad.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return homeAd;
    }
}

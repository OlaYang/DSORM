package com.meiqi.app.common.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiqi.app.action.BaseAction;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @ClassName: AppServiceMappingUtil
 * @Description:service mapping工具类
 * @author 杨永川
 * @date 2015年6月30日 下午4:23:46
 *
 */
public class AppActionMappingUtil {
    /**
     * 
     * @Title: getAppService
     * @Description:根据请求url获取app service类
     * @param @param url
     * @param @return
     * @return Object
     * @throws
     */
    @SuppressWarnings("rawtypes")
    public static BaseAction getAppService(String url) {
        // url为空，特殊处理
        if (StringUtils.isBlank(url)) {
            return (BaseAction) MyApplicationContextUtil.getBean("shopConfigAction");
        }

        Properties appServiceProperties = AppSysConfig.getAppServiceProperties();
        if (!CollectionsUtils.isNull(appServiceProperties)) {
            Iterator it = appServiceProperties.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = entry.getKey().toString();
                // 正则表达式匹配
                Pattern pattern = Pattern.compile(key);
                Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    String value = entry.getValue().toString();
                    return (BaseAction) MyApplicationContextUtil.getBean(value);
                } else if (url.contains(key)) {
                    String value = entry.getValue().toString();
                    return (BaseAction) MyApplicationContextUtil.getBean(value);
                }

            }
        }
        return null;
    }
    
    public static void main(String[] args) {
	    	String key="goods\\/\\d+\\/recommands";
	    	String url="goods/1/recommands";
    	  Pattern pattern = Pattern.compile(key);
          Matcher matcher = pattern.matcher(url);
          System.out.println(matcher.matches());
	}
}

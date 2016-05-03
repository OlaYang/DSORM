package com.meiqi.openservice.commons.util;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @ClassName: ServiceMappingUtil
 * @Description:service mapping工具类
 * @author zhouyongxiong
 * @date 2015年6月30日 下午4:23:46
 *
 */
public class ActionMappingUtil {
    /**
     * 
     * @Title: getService
     * @Description:根据请求url获取service类
     * @param @param url
     * @param @return
     * @return Object
     * @throws
     */
    @SuppressWarnings("rawtypes")
    public static BaseAction getService(String actionName) {
        if (!StringUtils.isBlank(actionName)) {
            return (BaseAction) MyApplicationContextUtil.getBean(actionName);
        }
        return null;
    }
}

package com.meiqi.app.common.utils;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * @ClassName: CollectionsUtil
 * @Description:
 * @author sky2.0
 * @date 2015年1月27日 下午11:03:18
 *
 */
public class CollectionsUtils extends CollectionUtils {

    /**
     * 
     * @Title: isNull
     * @Description:判断集合是null
     * @param @param collection
     * @param @return
     * @return boolean
     * @throws
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNull(Collection collection) {
        boolean result = true;
        if (null != collection && collection.size() > 0) {
            result = false;
        }
        return result;
    }



    /**
     * 
     * @Title: isNull
     * @Description:判断map是null
     * @param @param map
     * @param @return
     * @return boolean
     * @throws
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNull(Map map) {
        boolean result = true;
        if (null != map && map.size() > 0) {
            result = false;
        }
        return result;
    }



    /**
     * 
     * @Title: isNull
     * @Description:判断数组是null
     * @param @param Object
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isNull(Object[] Object) {
        boolean result = true;
        if (null != Object && Object.length > 0) {
            result = false;
        }
        return result;
    }
}
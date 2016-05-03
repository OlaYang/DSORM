package com.meiqi.app.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * 当把Person类作为BeanUtilTest的内部类时，程序出错<br>
 * java.lang.NoSuchMethodException: Property '**' has no setter method<br>
 * 本质：内部类 和 单独文件中的类的区别 <br>
 * BeanUtils.populate方法的限制：<br>
 * The class must be public, and provide a public constructor that accepts no
 * arguments. <br>
 * This allows tools and applications to dynamically create new instances of
 * your bean, <br>
 * without necessarily knowing what Java class name will be used ahead of time
 */

public class LejjBeanUtils extends BeanUtils {

    /**
     * 
     * @Title: transMapListToBeanList
     * @Description:Map-->Bean1: 利用org.apache.commons.beanutils 工具类实现 Map-->Bean
     * @param @param map
     * @param @param obj
     * @return void
     * @throws
     */
    public static void transMapListToBeanList(List<Map<String, Object>> mapList, List<Object> beanList, Class cls) {
        if (CollectionsUtils.isNull(mapList)) {
            return;
        }
        for (Map<String, Object> map : mapList) {
            Object obj = null;
            cls.isInstance(obj);
            transMapToBean(map, obj);
            if (null != obj) {
                beanList.add(obj);
            }
        }
    }



    /**
     * 
     * @Title: transMap2Bean
     * @Description:Map-->Bean1: 利用org.apache.commons.beanutils 工具类实现 Map-->Bean
     * @param @param map
     * @param @param obj
     * @return void
     * @throws
     */
    public static void transMapToBean(Map<String, Object> map, Object obj) {
        if (map == null || obj == null) {
            return;
        }
        try {
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 
     * @Title: transMapToBean2
     * @Description: Map-->Bean2: 利用Introspector,PropertyDescriptor实现 Map-->Bean
     * @param @param map
     * @param @param obj
     * @return void
     * @throws
     */
    public static void transMapToBean2(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;

    }



    /**
     * 
     * @Title: transBeanToMap
     * @Description:Bean-->Map: 利用Introspector和PropertyDescriptor 将Bean -->Map
     * @param @param obj
     * @param @return
     * @return Map<String,Object>
     * @throws
     */
    public static Map<String, Object> transBeanToMap(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }



    /**
     * 
     * @Title: copyProperties
     * @Description:
     * @param @param dest
     * @param @param orig
     * @return void
     * @throws
     */
    public static void copyProperties(Object dest, Object orig) {
        try {
            BeanUtilsBean.getInstance().copyProperties(dest, orig);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

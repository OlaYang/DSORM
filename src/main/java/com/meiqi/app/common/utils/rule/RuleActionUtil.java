package com.meiqi.app.common.utils.rule;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;

// import com.meilele.core.util.annotation.WithoutSet;
import com.meilele.datalayer.common.data.ActionCommand;
import com.meilele.datalayer.common.data.CommandManager;
import com.meilele.datalayer.common.data.response.CombinedActionResponse.ActionResult;

/**
 * 
 * @ClassName: RuleActionUtil
 * @Description:规则引擎INSERT UPDATE相关工具方法
 * @author 杨永川
 * @date 2015年5月16日 上午9:35:34
 *
 */
public final class RuleActionUtil {

    private static final Logger LOGGER = Logger.getLogger(RuleActionUtil.class);



    /**
     * INSERT
     * 
     * @param actionName
     * @param bean
     * @return
     */
    public static ActionResult excuteSaveRuleAction(String actionName, Object bean) {
        try {
            return buildSaveActionCommand(actionName, bean).executeResult();
        } catch (Exception e) {
            LOGGER.error(actionName + " error on " + bean.toString(), e);
        }
        return null;
    }



    public static ActionCommand buildSaveActionCommand(String actionName, Object bean) {
        ActionCommand actionCommand = CommandManager.getActionCommand(actionName);
        try {
            return (ActionCommand) actionCommand.setAsBean(bean);
        } catch (Exception e) {
            LOGGER.error(actionCommand.getActionName() + " error on " + bean.toString(), e);
        }
        return null;
    }



    /**
     * UPDATE
     * 
     * @param actionName
     * @param setMap
     * @param whereMap
     * @return
     */
    public static ActionResult excuteUpdateRuleAction(String actionName, Map<String, Object> setMap,
            Map<String, Object> whereMap) {
        try {
            return buildUpdateActionCommand(actionName, setMap, whereMap).executeResult();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(actionName + " error on " + setMap.toString() + ", " + whereMap.toString(), e);
        }
        return null;
    }



    public static ActionCommand buildUpdateActionCommand(String actionName, Map<String, Object> setMap,
            Map<String, Object> whereMap) {
        ActionCommand actionCommand = CommandManager.getActionCommand(actionName);
        Object value;
        for (Entry<String, Object> setEntry : setMap.entrySet()) {
            value = setEntry.getValue();
            if (null != value) {
                actionCommand.set(setEntry.getKey(), value);
            }
        }
        for (Entry<String, Object> whereEntry : whereMap.entrySet()) {
            value = whereEntry.getValue();
            if (null != value) {
                actionCommand.condition(whereEntry.getKey(), value);
            }
        }
        return actionCommand;
    }



    /**
     * bean转Map, 可排除加WithoutSet注解的属性
     * 
     * @param bean
     * @return
     */
    public static Map<String, Object> buildUpdateSet(Object bean) {
        Map<String, Object> setMap = new HashMap<String, Object>();
        try {
            Class<? extends Object> clazz = bean.getClass();
            Field[] fields = clazz.getDeclaredFields();
            String fieldName;
            Object fieldValue;
            for (Field field : fields) {
                // if (field.isAnnotationPresent(WithoutSet.class)) {
                // continue;
                // }
                fieldName = field.getName();
                fieldValue = BeanUtils.getProperty(bean, fieldName);
                if (null != fieldValue) {
                    setMap.put(fieldName, fieldValue);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return setMap;
    }



    /**
     * 适用于UPDATE SET字段过多
     * 
     * @param actionName
     * @param setBean
     * @return
     */
    public static ActionCommand buildUpdateActionCommand(String actionName, Object setBean) {
        ActionCommand actionCommand = CommandManager.getActionCommand(actionName);
        try {
            Class<? extends Object> clazz = setBean.getClass();
            Field[] fields = clazz.getDeclaredFields();
            String fieldName;
            Object fieldValue;
            for (Field field : fields) {
                // if (field.isAnnotationPresent(WithoutSet.class)) {
                // continue;
                // }
                fieldName = field.getName();
                fieldValue = BeanUtils.getProperty(setBean, fieldName);
                if (null != fieldValue) {
                    actionCommand.set(fieldName, fieldValue);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return actionCommand;
    }



    public static ActionResult excuteUpdateRuleAction(ActionCommand actionCommand) {
        try {
            return actionCommand.executeResult();
        } catch (Exception e) {
            LOGGER.error(actionCommand.getActionName() + " error", e);
        }
        return null;
    }
}

package com.meiqi.data.engine.functions.matlab;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-21
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public class MatlabReqInfo {
    private String operationType;   //操作类型,如 plot作图等
    private String functionName;
    private Map<String,List<Object>> data;
    private Map<String,Object> param;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Map<String, List<Object>> getData() {
        return data;
    }

    public void setData(Map<String, List<Object>> data) {
        this.data = data;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}

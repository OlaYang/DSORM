package com.meiqi.data.engine.excel;

import java.util.HashMap;
import java.util.Map;

import com.meiqi.data.engine.D2Data;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;

/**
 * User: 
 * Date: 13-11-1
 * Time: 上午10:16
 */
public class CalInfo {
    final TService servicePo;
    final Map<String, Object> param;
    final Map<String, Map<Object, Object>> cache4_S = new HashMap<String, Map<Object, Object>>();
    final int callLayer;
    D2Data curD2data;
    TServiceColumn curColumnPo;
    String serviceName;
    String columnName;
    int curRow;
    int curColumn;
    int maxRow;

    public int getCurColumn() {
        return curColumn;
    }

    public int getCurRow() {
        return curRow;
    }

    public Map<Object, Object> getCache(String name) {
        Map<Object, Object> result = cache4_S.get(name);

        if (result == null) {
            result = new HashMap<Object, Object>();
            cache4_S.put(name, result);
        }

        return result;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public void setCurD2data(D2Data curD2data) {
        this.curD2data = curD2data;
    }

    public CalInfo(int callLayer, Map<String, Object> param, TService servicePo) {
        if (param == null || servicePo == null) {
            throw new NullPointerException("param or servicePo");
        }

        this.callLayer = callLayer;
        this.param = param;
        this.servicePo = servicePo;
        this.serviceName = servicePo.getName();
    }

    public int getCallLayer() {
        return callLayer;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public TService getServicePo() {
        return servicePo;
    }

	public TServiceColumn getCurColumnPo() {
		return curColumnPo;
	}

	public void setCurColumnPo(TServiceColumn curColumnPo) {
		this.curColumnPo = curColumnPo;
	}

	public Map<String, Map<Object, Object>> getCache4_S() {
		return cache4_S;
	}

	public D2Data getCurD2data() {
		return curD2data;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setCurRow(int curRow) {
		this.curRow = curRow;
	}

	public void setCurColumn(int curColumn) {
		this.curColumn = curColumn;
	}

    
}

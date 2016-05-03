package com.meiqi.data.engine;

import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;

import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-6-21
 * Time: 上午11:55
 * data 中第一维度是行，第二维度是列
 */
public class D2Data {
    private final List<TServiceColumn> columnList;
    private Object[][] data = new Object[0][];
    long createtime = 0;
    long latency = Long.MAX_VALUE;
    Map<String, Object> parameter;
    String serviceName;
    long timestamp = 0;
    boolean isInReload = false;
    long lastAcTime = 0L;


    void setProcessInfo(long createtime, long latency
            , Map<String, Object> parameter
            , String serviceName, long timestamp, long lastAcTime) {
        this.createtime = createtime;
        this.latency = latency;
        this.parameter = parameter;
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.lastAcTime = lastAcTime;
    }


    public D2Data(List<TServiceColumn> columnList) {
        this.columnList = columnList;
    }

    public List<TServiceColumn> getColumnList() {
        return columnList;
    }

    @Override
    public String toString() {
        return "D2Data{" +
                "columnList=" + columnList +
                ", data len=" + data.length +
                '}';
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }

    /**
     * 获取单元格值
     *
     * @param name 列名称
     * @param row  行数
     * @return
     */
    public Object getValue(String name, int row) throws RengineException {
        if (row < data.length) {
            for (int i = 0; i < columnList.size(); i++) {
                final TServiceColumn column = columnList.get(i);
                if (column.getColumnName().equals(name)) {
                    return data[row][column.getColumnIntIndex()];
                }
            }
        }

        return null;
    }


    public Object getValue(int column, int row) {
        try {
            return data[row][column];
        } catch (Exception e) {
            //
        }

        return null;
    }

}

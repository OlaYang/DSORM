package com.meiqi.data.engine.weka;

import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: 
 * Date: 14-1-20
 * Time: 上午10:56
 */
public class ArffUtils {

    public static final String d2Data2Arff4NUM(D2Data data, String columnNames) throws RengineException {
        if (columnNames == null) {
            throw new RengineException(null, "分析列为空");
        }

        String[] attributes = columnNames.split("[ ,]+");
        ArrayList<String> attList = new ArrayList<String>(attributes.length);
        ArrayList<Integer> attIndex = new ArrayList<Integer>(attributes.length);

        for (String attribute : attributes) {
            if (attribute.length() == 0) {
                continue;
            }

            boolean found = false;
            for (TServiceColumn columnPo : data.getColumnList()) {
                if (columnPo.getColumnName().equals(attribute)) {
                    attList.add(attribute);
                    attIndex.add(columnPo.getColumnIntIndex());
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RengineException(null, "分析的列:" + attribute + "不存在");
            }
        }

        if (attList.size() == 0) {
            throw new RengineException(null, "未找到分析的列信息");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("@RELATION rengine").append("\r\n").append("\r\n");

        for (String attribute : attList) {
            builder.append("@ATTRIBUTE ").append(attribute).append(" ").append("NUMERIC").append("\r\n");
        }

        builder.append("\r\n@DATA\r\n");
        Object[][] d2 = data.getData();
        for (int i = 0, size = d2.length; i < size; i++) {
            boolean first = true;
            for (int index : attIndex) {
                Object obj = d2[i][index];

                if (first) {
                    first = false;
                } else {
                    builder.append(",");
                }

                try {
                    double value = DataUtil.getNumberValue(obj).doubleValue();
                    builder.append(DataUtil.number2String(value));
                } catch (CalculateError err) {
                    builder.append("?");
                }
            }

            builder.append("\r\n");
        }

        return builder.toString();
    }

    public static final String rows2Arff4NOMINAL(HashMap<String, LinkedHashMap<String, String>> rows)
            throws RengineException {
        StringBuilder builder = new StringBuilder();
        builder.append("@RELATION rengine").append("\r\n").append("\r\n");
        LinkedHashMap<String, String> columns = rows.get("");

        for (String attribute : columns.keySet()) {
            builder.append("@ATTRIBUTE ")
                    .append(attribute).append(" ")
                    .append("{t}\r\n");
        }

        builder.append("\r\n@DATA\r\n");

        for (Map.Entry<String, LinkedHashMap<String, String>> entry : rows.entrySet()) {
            final LinkedHashMap<String, String> row = entry.getValue();
            boolean first = true;
            for (String column : columns.keySet()) {
                final String value = row.get(column);

                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }

                if (value == null) {
                    builder.append('?');
                } else {
                    builder.append('t');
                }
            }

            builder.append("\r\n");
        }

        return builder.toString();
    }

}

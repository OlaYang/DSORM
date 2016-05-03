package com.meiqi.data.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.util.LogUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年2月22日 下午1:34:10 
 * 类说明   将传入的json参数反解析为二维数组（微信基础数据源）
 */

public class CacheWeiXinBaseService {

	@SuppressWarnings("rawtypes")
	public static D2Data getWeiXinDate(TService servicePo, Map<String, Object> parameter){
		List<TServiceColumn> columnList = new ArrayList<TServiceColumn>();
		Map<String,String> map = new HashMap<String, String>();
		
		JSONArray json_info = JSONArray.parseArray(parameter.get("wx_json_info").toString());
		int maxRow = json_info.size();
		Object[][] data = null;
		for (int i = 0; i < json_info.size(); i++) {
			Iterator iter = json_info.getJSONObject(i).entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    map.put(entry.getKey().toString(), "");
			}
		}
		int num = 0;
		for (String str : map.keySet()) {
			TServiceColumn tServiceColumn = new TServiceColumn();
			tServiceColumn.setColumnName(str);
			tServiceColumn.setFormula("and(0)");
			tServiceColumn.setIsTransfer("1");
			tServiceColumn.setColumnIndex(DataUtil.extract(num));
			columnList.add(tServiceColumn);
			num = num +1;
		}
		D2Data d2Data = new D2Data(columnList);
		
		if (maxRow != 0) {
            int maxColumnIndex = -1;
            for (TServiceColumn column : columnList) {
                final int index;
                try {
					index = column.getColumnIntIndex();
					maxColumnIndex = index > maxColumnIndex ? index : maxColumnIndex;
				} catch (RengineException e) {
					e.getMessage();
				}
            }

            int needColumnSize = maxColumnIndex + 1; // 实际需要的列数目

            data = new Object[maxRow][];
            Object[] rowData;
            long start = System.currentTimeMillis();
            for (int i = 0; i < json_info.size(); i++) {
            	rowData = new Object[needColumnSize];
            	for (int j = 0; j < columnList.size(); j++) {
                	String str = columnList.get(j).getColumnName();
                	rowData[j] = json_info.getJSONObject(i).get(str);
    			}
            	data[i] = rowData;
    		}
            long spendTime = System.currentTimeMillis() - start;
            LogUtil.info(Thread.currentThread().getName() + " arraycopy time" + spendTime);
            d2Data.setData(data);
        }
		return d2Data;
		
	}
}

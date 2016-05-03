package com.meiqi.data.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.ExcelEngineTool;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;
import com.meiqi.data.util.LogUtil;

public class Cache4WXService {

	static D2Data getD2Data(TService servicePo, Map<String, Object> param, int callLayer) throws RengineException {
		//检查是否该数据源包含列
		if (servicePo.getColumns().size() == 0) {
            throw new RengineException(servicePo.getName(), "列数目为0, ");
        }
		
		//获取数据源的列名
		final String serviceName = servicePo.getName();
		
		final long start = System.currentTimeMillis();
        final long currentSecond = start / 1000;
        final int cacheTime = servicePo.getCacheTime() == null ? 0 : servicePo.getCacheTime();
        final long updateTimestamp = servicePo.getUpdateTime() == null ? 0L : servicePo.getUpdateTime().getTime();
        String key = JSON.toJSONString(param);
        
        D2Data cacheData = Cache4AdvanceService.checkAdvanceGlobalCache(serviceName, cacheTime, key, updateTimestamp, start, currentSecond);
        if(null == cacheData){
        	final Integer baseServiceID = servicePo.getBaseServiceID();
            final String baseServicename = Services.id2Name(baseServiceID);
            final TService baseServicePo = Services.getService(baseServicename);
            if (baseServicename == null || baseServicePo == null) {
                throw new RengineException(serviceName, "基本数据源ID: " + baseServiceID + " 未找到");
            }
            CalInfo calInfo = new CalInfo(callLayer, param, servicePo);
            try {
                final D2Data baseD2Data =
                        Cache4D2Data.getD2Data(baseServicePo, param, callLayer, servicePo, param, "父级调用");
                final D2Data d2Data = cal(baseD2Data, calInfo, servicePo);
                calInfo = null;
                long latency = System.currentTimeMillis() - start; // llcheng
                if (LogUtil.isDebugEnabled()) {
                    LogUtil.debug("rows " + d2Data.getData().length + ", columns: " + d2Data.getColumnList().size());
                }

                Cache4AdvanceService.saveAdvanceGlobalCache(serviceName, cacheTime, key, d2Data, latency, currentSecond, param, updateTimestamp, start);// llcheng

                return d2Data;
            } catch (RengineException e) {
                LogUtil.error("Cache4AdvanceService getD2Data error:"+e.getMessage());
                e.addInvoke(calInfo);
                throw e;
            }
        }else{
        	return cacheData;
        }
        
		
	}
	
	
	private static D2Data cal(D2Data baseD2Data, CalInfo calInfo, TService servicePo)
            throws RengineException {
        Object[][] basedata = baseD2Data.getData();
        Object[][] data = basedata;
        final int maxRow = basedata.length;

        final List<TServiceColumn> columnList = new ArrayList<TServiceColumn>(
                baseD2Data.getColumnList().size() + servicePo.getColumns().size()+1);
        final D2Data d2Data = new D2Data(columnList);
        d2Data.setData(data);

        for (TServiceColumn column : baseD2Data.getColumnList()) {
            columnList.add(column);
        }
        List<TServiceColumn> list=servicePo.getColumns();
        for (TServiceColumn column : list) {
            columnList.add(column);
        }
        
        TServiceColumn lastColumn=list.get(list.size()-1);
        TServiceColumn jsonColumn=new TServiceColumn();
        jsonColumn.setColumnName("resultJson");
        jsonColumn.setFormula("and(0)");
        jsonColumn.setIsTransfer("1");
        jsonColumn.setColumnIndex(DataUtil.extract(lastColumn.getColumnIntIndex()+1));
        columnList.add(jsonColumn);
        if (maxRow != 0) {  // 有数据，进行COPY和扩容
            int maxColumnIndex = -1;
            for (TServiceColumn column : columnList) {
                final int index;
                index = column.getColumnIntIndex();
                maxColumnIndex = index > maxColumnIndex ? index : maxColumnIndex;
            }

            int needColumnSize = maxColumnIndex + 1; // 实际需要的列数目

            data = new Object[maxRow][];
            Object[] rowData;
            long start = System.currentTimeMillis();
            for (int i = 0; i < maxRow; i++) {
                rowData = new Object[needColumnSize];
                System.arraycopy(basedata[i], 0, rowData, 0, basedata[i].length); // 复制原有数据, 原有数据可能被多个高级数据源使用
                data[i] = rowData;
            }
//            long spendTime = System.currentTimeMillis() - start;
//            LogUtil.info(Thread.currentThread().getName() + " arraycopy time" + spendTime);
            d2Data.setData(data);

            calInfo.setCurD2data(d2Data);
            calInfo.setMaxRow(maxRow);

            ExcelEngineTool.process(d2Data, calInfo);
            
            //获取计算后行数
            Object[][] object2=d2Data.getData();
            final int dataLength=object2.length;
            //将列存放如map
            ConcurrentHashMap<String, TServiceColumn> columnMap=new ConcurrentHashMap<String, TServiceColumn>();
    		for(TServiceColumn tServiceColumn:columnList){
    			columnMap.put(tServiceColumn.getColumnName(), tServiceColumn);
    		}
            //遍历每一行，开始拼装json
            for(int rowNum=0;rowNum<dataLength;rowNum++){
            	JSONObject json=formatJson(servicePo,columnList,columnMap,object2[rowNum]);
            	object2[rowNum][jsonColumn.getColumnIntIndex()]=json.toJSONString();
             }
//            d2Data.setData(object2);
        }

        return d2Data;
    }
	
	private static JSONObject formatJson(TService servicePo,List<TServiceColumn> columnList,ConcurrentHashMap<String, TServiceColumn> columnMap,Object[] obj) throws RengineException{
		JSONObject json=new JSONObject();
		for(TServiceColumn tServiceColumn:columnList){
			final String columnname=tServiceColumn.getColumnName();
			if(!json.containsKey(columnname)&&"1".equals(tServiceColumn.getIsTransfer())){
				addtoJson(servicePo, tServiceColumn, obj, json, columnMap, "", null);
			}
			
		}
		return json;
	}
	
	private static void addtoJson(TService servicePo,TServiceColumn tServiceColumn,Object[] obj,JSONObject json,ConcurrentHashMap<String, TServiceColumn> columnMap,String fatherobjName,Object fatherObj) throws RengineException{
		final String columnname=tServiceColumn.getColumnName();
		final String column_type=tServiceColumn.getColumn_type();
		final String parent_column=tServiceColumn.getParent_column();
		final String formulaName=tServiceColumn.getFormula();
		if(null==formulaName||"".equals(formulaName)){
			return;
		}
		if("o".equalsIgnoreCase(column_type)){
			JSONObject tempjJsonObject=null;
			if(json.containsKey(columnname)){
				tempjJsonObject=json.getJSONObject(columnname);
			}else{
				tempjJsonObject=new JSONObject();
				String val=String.valueOf(obj[tServiceColumn.getColumnIntIndex()]);
				if(null!=val&&!"0".equals(val)&&!"".equals(val)){
					if(!val.startsWith("{")&&!val.endsWith("}")){
						val="{"+val+"}";
					}
					try{
						tempjJsonObject=JSONObject.parseObject(val);
					}catch(Exception e){
						throw new RengineException(servicePo.getName(), "列"+columnname+"的参数不是json");
					}
					
				}
			}
			if(null!=fatherObj){
				if(!"".equals(fatherObj.toString())){
					tempjJsonObject.put(fatherobjName, fatherObj);
				}
			}
			if(null==parent_column||"".equals(parent_column)){
				if(!json.containsKey(columnname)){
					json.put(columnname, tempjJsonObject);
				}
			}else{
				if(columnMap.containsKey(parent_column)){
					addtoJson(servicePo, columnMap.get(parent_column),obj, json, columnMap, columnname,tempjJsonObject);
				}else{
					throw new RengineException(servicePo.getName(), "列"+columnname+"的 父列未找到");
				}
			}
			
			
		}
		else if("a".equalsIgnoreCase(column_type)){
			String val=(String) obj[tServiceColumn.getColumnIntIndex()];
			JSONArray tempjJsonArray=null;
			if(json.containsKey(columnname)){
				tempjJsonArray=json.getJSONArray(columnname);
			}else{
				if(null==val){
					return;
				}
				if(!val.startsWith("[")&&!val.endsWith("]")){
					val="["+val+"]";
				}
				tempjJsonArray=JSONArray.parseArray(val);
				
			}
			
			 
			if(null!=fatherObj){
				tempjJsonArray.add(fatherObj);
			}
			if(null==parent_column||"".equals(parent_column)){
				if(!json.containsKey(columnname)){
					json.put(columnname, tempjJsonArray);
				}
			}else{
				if(columnMap.containsKey(parent_column)){
					addtoJson(servicePo, columnMap.get(parent_column), obj,json, columnMap, columnname,tempjJsonArray);
				}else{
					throw new RengineException(servicePo.getName(), "列"+columnname+"的 父列未找到");
				}
			}
			
		}
		else{
			final Object val=obj[tServiceColumn.getColumnIntIndex()];
			if(null==parent_column||"".equals(parent_column)){
				if(!json.containsKey(columnname)){
					if(!"".equals(val)){
						json.put(columnname, val);
					}
				}
			}else{
				if(columnMap.containsKey(parent_column)){
					addtoJson(servicePo, columnMap.get(parent_column), obj,json, columnMap, columnname,val);
				}else{
					throw new RengineException(servicePo.getName(), "列"+columnname+"的 父列未找到");
				}
			}
			
		}
	}
}

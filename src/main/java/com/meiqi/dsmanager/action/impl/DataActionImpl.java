/**   
 * @Title: DataServiceImpl.java 
 * @Package com.meiqi.dsmanager.service.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author yangyong
 * @date 2015年6月24日 上午11:31:45 
 * @version V1.0   
 */
package com.meiqi.dsmanager.action.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ListUtil;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.user.handler.OrderRow;
import com.meiqi.dsmanager.action.ICacheAction;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.dsmanager.common.config.CacheLevelConfig;
import com.meiqi.dsmanager.common.config.DataFormatConfig;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.dao.IDataSourcesDao;
import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.FormatRespInfoUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.commons.util.RuleExceptionUtil;

/**
 * @ClassName: DataServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yangyong
 * @date 2015年6月24日 上午11:31:45
 * 
 */

@Service("dataAction")
public class DataActionImpl implements IDataAction {

    @Autowired
    private IDataSourcesDao dataSourcesDao;

    @Autowired
    private ICacheAction cacheAction;
    
    @Autowired
    private RuleExceptionUtil ruleExceptionUtil;

    @Autowired
    private IMemcacheAction memcacheService;
    
    /**
     * 批量调用规则,目前限制一次只接受最多10个
     */
    public String getDatas(DsManageReqInfo reqInfo, String keyContent) {
        RuleServiceResponseData respInfo = new RuleServiceResponseData();
        List<DsManageReqInfo> serviceNames = reqInfo.getServices();
        if(CollectionsUtils.isNull(serviceNames) || serviceNames.size()>10){
            respInfo.setCode(DsResponseCodeData.RULE_SIZE_GT_TEN.code);
            respInfo.setDescription(DsResponseCodeData.RULE_SIZE_GT_TEN.description);
            return JSON.toJSONString(respInfo);
        }
        int serviceNamesSize = serviceNames.size();
        List<List<Map<String, String>>> rowsList = new ArrayList<List<Map<String, String>>>(serviceNames.size());
        for (int i = 0; i < serviceNamesSize; i++) {
            DsManageReqInfo req = serviceNames.get(i);
            DataSources dataSource = getDataSource(req.getServiceName(), req.getStyleSn());
            RuleServiceResponseData ruleServiceResponseData = getData(req, dataSource);
            // 如果其中一个ser发生错误则返回，并且不返还数据
            if ("1".equals(ruleServiceResponseData.getCode())) {
                respInfo.setCode(ruleServiceResponseData.getCode());
                respInfo.setDescription(req.getServiceName() + ":" + ruleServiceResponseData.getDescription());
                rowsList.clear();
                break;
            }
            rowsList.add(ruleServiceResponseData.getRows());
        }
        respInfo.setRowsList(rowsList);
        String formatString = FormatRespInfoUtil.format(reqInfo, respInfo, null);
        return formatString;
    }
    

    /**
     * 用于内部调用的getData方法
    * @Title: getInnerData 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param reqInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public String getInnerData(DsManageReqInfo reqInfo){
        DataSources dataSource = getDataSource(reqInfo.getServiceName(), reqInfo.getStyleSn());
        RuleServiceResponseData respInfo = getData(reqInfo, dataSource);
        String formatString=FormatRespInfoUtil.format(reqInfo, respInfo, dataSource);
        return formatString;
    };
    /*
     * Title: getData Description:
     * 
     * @param reqInfo
     * 
     * @return
     * 
     * @see
     * com.meiqi.dsmanager.service.IDataService#getData(com.meiqi.dsmanager.
     * po.dsmanager.DsManageReqInfo)
     */
    
    @Override
    public String getData(DsManageReqInfo dsReqInfo){
        if(dsReqInfo.getParam()==null){
            dsReqInfo.setParam(new HashMap<String, Object>());
        }
        if(dsReqInfo.getParam().get("site_id")==null || "".equals(dsReqInfo.getParam().get("site_id").toString())){
            dsReqInfo.getParam().put("site_id", "0");//如果没有则放默认值0，代表乐家居，优家购
        }
        if(!"2".equals(dsReqInfo.getAccessFlag())){
            //新需求，功能 #7218商城共享设置-需在数据源统一传入show_shop
            DsManageReqInfo dsReqInfoTmp=new DsManageReqInfo();
            dsReqInfoTmp.setServiceName("T_BUV1_ShowshopSiteRefer");
            Map<String,Object> param=new HashMap<String, Object>();
            param.put("site_id",dsReqInfo.getParam().get("site_id"));
            dsReqInfoTmp.setParam(param);
            String resultTmp=getInnerData(dsReqInfoTmp);
            RuleServiceResponseData responseData1;
            responseData1 = com.meiqi.openservice.commons.util.DataUtil.parse(resultTmp, RuleServiceResponseData.class);
            if(!"0".equals(responseData1.getCode())){
                return resultTmp;
            }
            List<Map<String, String>> list1=responseData1.getRows();
            if(com.meiqi.dsmanager.util.ListUtil.notEmpty(list1)){
                String show_shop=""; 
                for(Map<String, String> map:list1){
                    if(map.get("show_shop")!=null){
                        show_shop=map.get("show_shop");
                        dsReqInfo.getParam().put("show_shop", show_shop);
                        break;
                    }
                }
            }
        }
        dsReqInfo.getParam().put("site_id", dsReqInfo.getParam().get("site_id"));
        DataSources dataSource = getDataSource(dsReqInfo.getServiceName(), dsReqInfo.getStyleSn());
        RuleServiceResponseData respInfo = getData(dsReqInfo, dataSource);
        String formatString=FormatRespInfoUtil.format(dsReqInfo, respInfo, dataSource);
        return formatString;
    };
    
    @Override
    public String getData(DsManageReqInfo dsReqInfo,String keyContent) {
        if(dsReqInfo.getParam()==null){
            dsReqInfo.setParam(new HashMap<String, Object>());
        }
        if(dsReqInfo.getParam().get("site_id")==null || "".equals(dsReqInfo.getParam().get("site_id").toString())){
            dsReqInfo.getParam().put("site_id", "0");//如果没有则放默认值0，代表乐家居，优家购
        }
        if(!"2".equals(dsReqInfo.getAccessFlag())){
            //新需求，功能 #7218商城共享设置-需在数据源统一传入show_shop
            DsManageReqInfo dsReqInfoTmp=new DsManageReqInfo();
            dsReqInfoTmp.setServiceName("T_BUV1_ShowshopSiteRefer");
            Map<String,Object> param=new HashMap<String, Object>();
            param.put("site_id",dsReqInfo.getParam().get("site_id"));
            dsReqInfoTmp.setParam(param);
            String resultTmp=getInnerData(dsReqInfoTmp);
            RuleServiceResponseData responseData1;
            responseData1 = com.meiqi.openservice.commons.util.DataUtil.parse(resultTmp, RuleServiceResponseData.class);
            if(!"0".equals(responseData1.getCode())){
                return resultTmp;
            }
            List<Map<String, String>> list1=responseData1.getRows();
            if(com.meiqi.dsmanager.util.ListUtil.notEmpty(list1)){
                String show_shop=""; 
                for(Map<String, String> map:list1){
                    if(map.get("show_shop")!=null){
                        show_shop=map.get("show_shop");
                        dsReqInfo.getParam().put("show_shop", show_shop);
                        break;
                    }
                }
            }
        }
        dsReqInfo.getParam().put("site_id", dsReqInfo.getParam().get("site_id"));
        DataSources dataSource = getDataSource(dsReqInfo.getServiceName(), dsReqInfo.getStyleSn());
        RuleServiceResponseData respInfo = getData(dsReqInfo, dataSource);
        String formatString=FormatRespInfoUtil.format(dsReqInfo, respInfo, dataSource);
        if(ListUtil.notEmpty(respInfo.getRows()) || ListUtil.notEmpty(respInfo.getRowsList())){
            if(StringUtils.isNotEmpty(keyContent) && (CacheLevelConfig.MEMCACHE_CACHE.level == dataSource.getCacheLevel())){
                cacheAction.postCache(dsReqInfo, respInfo, dataSource,keyContent,formatString);
            }
        }
        return formatString;
    }
    

    public RuleServiceResponseData getData(DsManageReqInfo reqInfo, DataSources dataSource) {
        RuleServiceResponseData respInfo = new RuleServiceResponseData();
        String serviceName = reqInfo.getServiceName();
        final Map<String, Object> param = reqInfo.getParam();

        boolean needAll = "1".equals(reqInfo.getNeedAll());

        TService info = Services.getService(serviceName);

        
        
        if(null== dataSource){
            respInfo.setCode(DsResponseCodeData.NOT_SET_DATASOURCE.code);
            respInfo.setDescription(DsResponseCodeData.NOT_SET_DATASOURCE.description);
            return respInfo;
        }
        
        if (null == serviceName || null == info) {
            respInfo.setCode(DsResponseCodeData.NOT_SET_DATASOURCE.code);
            respInfo.setDescription(DsResponseCodeData.NOT_SET_DATASOURCE.description);
            return respInfo;
        }

        if ("LS".equals(info.getState())) {
            respInfo.setCode(DsResponseCodeData.LS_DATASOURCE.code);
            respInfo.setDescription(DsResponseCodeData.LS_DATASOURCE.description);
            return respInfo;
        }

        boolean isDbLangZH = true;
        if (reqInfo.getDbLang() != null) {
            isDbLangZH = reqInfo.getDbLang().trim().equalsIgnoreCase("zh");
        }

        final String orderColumnName = reqInfo.getOrderColumnName();
        final String order = reqInfo.getOrder();
        // 检查到需要到规则引擎获取数据
        try {
            boolean isBaseService=info.getBaseServiceID()==null?true:false;
            D2Data data = DataUtil.getD2Data(info, param);
            if(DataFormatConfig.EXCEL.format.equalsIgnoreCase(reqInfo.getFormat())){
                respInfo.setExcelByte(data2Excel(isBaseService,data, info.getColumns(), needAll, reqInfo));
            }else{
                respInfo.setRows(data2Rows(isBaseService,data, info.getColumns(), needAll, reqInfo, isDbLangZH, orderColumnName, order));
            }
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        } catch (Exception e) {
            e.printStackTrace();
            respInfo.setCode("1");
            respInfo.setDescription("data系统错误:" + e.getMessage());
            LogUtil.error("DataActionImpl getData time:"+e.getMessage());
            ruleExceptionUtil.run(e);
        }
        return respInfo;
    }

    /**
     * 根据规则配置的刷选条件进行过滤数据
    * @Title: dataRebuildVerify 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param ruleExportConditionConfigure
    * @param @param field_name
    * @param @param currentValue
    * @param @return  参数说明 
    * @return boolean    返回类型 
    * @throws
     */
    public boolean dataRebuildVerify(RuleServiceResponseData ruleExportConditionConfigure,String field_name,Object currentValueTmp){
        
        List<Map<String, String>> rows1=ruleExportConditionConfigure.getRows();
        boolean removeFlag=true;
        try {
            for(Map<String, String> r:rows1){
                if(!field_name.equals(r.get("field_name"))){
                    continue;
                }
                String condition=r.get("condition");
                String value_type=r.get("value_type");
                String value=r.get("value");
                String currentValue="";
                if(currentValueTmp!=null){
                    currentValue=currentValueTmp.toString();
                }
                if(StringUtils.isNotEmpty(field_name) && StringUtils.isNotEmpty(condition) && StringUtils.isNotEmpty(value_type) && value!=null){
                    if("string".equals(value_type)){
                        //如果是字符串类型
                        if("=".equals(condition)){
                            if(!(currentValue.equals(value))){
                                removeFlag=false;
                            }
                        }else if("!=".equals(condition)){
                            if(currentValue.equals(value)){
                                removeFlag=false;
                            }
                        }else if("contain".equals(condition)){
                            if(!currentValue.contains(value)){
                                removeFlag=false;
                            }
                        }else if("not_contain".equals(condition)){
                            if(currentValue.contains(value)){
                                removeFlag=false;
                            }
                        }
                    }else  if("number".equals(value_type)){
                        if(StringUtils.isEmpty(currentValue) || StringUtils.isEmpty(value)){
                            removeFlag=false;
                            return removeFlag;
                        }
                        //如果是数字类型
                        float cv=Float.valueOf(currentValue).floatValue();
                        float v=Float.valueOf(value).floatValue();
                        if(">".equals(condition)){
                            if(!(cv > v)){
                                removeFlag=false;
                            }
                        }else if("<".equals(condition)){
                            if(!(cv < v)){
                                removeFlag=false;
                            }
                        }else if(">=".equals(condition)){
                            if(!(cv >= v)){
                                removeFlag=false;
                            }
                        }else if("<=".equals(condition)){
                            if(!(cv <= v)){
                                removeFlag=false;
                            }
                        }else if("=".equals(condition)){
                            if(!(cv == v)){
                                removeFlag=false;
                            }
                        }else if("!=".equals(condition)){
                            if(!(cv != v)){
                                removeFlag=false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("dataRebuildVerify error:"+e);
            removeFlag=false;
            return removeFlag;
        }
        return removeFlag;
    }
    
    
    private byte[] data2Excel(boolean isBaseService,D2Data data, List<TServiceColumn> columnsNeed, boolean needAll,DsManageReqInfo reqInfo) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int maxRow = data.getData().length;
        
        if (maxRow != 0) {
            int maxColumn = data.getData()[0].length;
            TServiceColumn[] columns = new TServiceColumn[maxColumn];
            if(!isBaseService){
                if(StringUtils.isNotEmpty(reqInfo.getReturnFilter())){
                    columnsNeed=getColumnFilter(data, reqInfo.getReturnFilter());
                }else if(needAll){
                    columnsNeed=data.getColumnList();
                }
            }else{
                columnsNeed=data.getColumnList();
            }
            for (TServiceColumn column : columnsNeed) {
                if ("1".equals(column.getIsTransfer())) {
                    columns[column.getColumnIntIndex()] = column;
                }
            }
            for (int i = 0; i < maxColumn; i++) {
                final TServiceColumn column = columns[i];
                if (column != null) {
                	String indexName="";
                	if(column.getDescription()==null || "".equals(column.getDescription().trim())){
                		indexName=column.getColumnName();
                	}else{
                		indexName=column.getDescription();
                	}
                    writeString(column.getColumnIndex() + "-" +indexName, baos);
                }
            }

            writeLine(baos);

            //是否需要根据规则配置的刷选条件过滤数据
            if(reqInfo.isNeedVerifyAndRebuildData()){
                DsManageReqInfo serviceReqInfo1=new DsManageReqInfo();
                serviceReqInfo1.setServiceName("YJG_BUV1_ConditionConfigure");
                Map<String,Object> param1=new HashMap<String, Object>();
                param1.put("rule_name", reqInfo.getServiceName());
                serviceReqInfo1.setParam(param1);
                serviceReqInfo1.setNeedAll("1");
                String data1 =getInnerData(serviceReqInfo1);
                RuleServiceResponseData ruleExportConditionConfigure=null;
                ruleExportConditionConfigure=DataUtil.parse(data1,RuleServiceResponseData.class);
                if(ruleExportConditionConfigure!=null && ListUtil.notEmpty(ruleExportConditionConfigure.getRows())){
                    for (int i = 0; i < data.getData().length; i++) {
                        final Object[] row = data.getData()[i];
                        boolean verifyResult=true;
                        StringBuffer msg=new StringBuffer();
                        for (int j = 0; j < row.length; j++) {
                            if (columns[j] == null) {
                                continue;
                            }
                            final Object value = row[j];
                            TServiceColumn column=columns[j];
                            if(column!=null){
                                    String column_name=column.getColumnName();
                                    if(StringUtils.isNotEmpty(column_name)){
                                      //需要根据规则配置的刷选条件过滤数据
                                      verifyResult=dataRebuildVerify(ruleExportConditionConfigure, column_name, value);
                                    }
                            }
                            if(!verifyResult){
                                //ExportLOG.info("serviceName:"+reqInfo.getServiceName()+",export verifyResult:"+verifyResult+",column_name:"+column.getColumnName()+",value:"+value+",row:"+JSONObject.toJSONString(row));
                                break;
                            }
                            String str=DataUtil.getStringValue(value);
                            str=("\"" + str.replace("\"", "\"\"") + "\"");
                            msg.append(str).append(new String(buffd));
                        }
                        if(!verifyResult){
                            continue;
                        }else{
                            //ExportLOG.info("serviceName:"+reqInfo.getServiceName()+",export verifyResult:"+verifyResult+",row:"+JSONObject.toJSONString(row));
                            baos.write(msg.toString().getBytes("GBK"));
                        }
                        writeLine(baos);
                    }
                }else{
                    for (int i = 0; i < data.getData().length; i++) {
                        final Object[] row = data.getData()[i];

                        for (int j = 0; j < row.length; j++) {
                            if (columns[j] == null) {
                                continue;
                            }

                            final Object value = row[j];
                            
                            writeString(DataUtil.getStringValue(value), baos);
                        }
                        writeLine(baos);
                    }
                }
            }else{
                for (int i = 0; i < data.getData().length; i++) {
                    final Object[] row = data.getData()[i];

                    for (int j = 0; j < row.length; j++) {
                        if (columns[j] == null) {
                            continue;
                        }

                        final Object value = row[j];
                        
                        writeString(DataUtil.getStringValue(value), baos);
                    }
                    writeLine(baos);
                }
            }
        } else {
            reqInfo.setNoDataFlag(true);
            writeString("无任何结果", baos);
            writeLine(baos);
        }

        baos.flush();
        return baos.toByteArray();
    }

    static byte[] buffd = ",".getBytes(Charset.forName("GBK"));
    static byte[] buffl = "\r\n".getBytes(Charset.forName("GBK"));

    private void writeString(String s, ByteArrayOutputStream baos) throws IOException {
        String str=("\"" + s.replace("\"", "\"\"") + "\"");
        baos.write(str.getBytes(Charset.forName("GBK")));
        baos.write(buffd);
    }

    private void writeLine(ByteArrayOutputStream baos) throws IOException {
        baos.write(buffl);
    }
    
    
    /**
     * 将d2data转为List<Map>格式, 可灵活转为csv和xml
     *
     * @param data
     * @param columnsNeed
     * @param needAll
     * @param isDbLangZH
     * @param orderColumnName
     * @param order
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> data2Rows(boolean isBaseService,D2Data data, List<TServiceColumn> columnsNeed, boolean needAll, DsManageReqInfo reqInfo, boolean isDbLangZH, String orderColumnName, String order) throws Exception {
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>(data.getData().length);
        if (data.getData().length == 0) {
            return rows;
        }

        Map<String, String> rowData;
        TServiceColumn[] columns = new TServiceColumn[data.getData()[0].length];
        int orderColumnInt = -1;
        if(!isBaseService ){
            if(StringUtils.isNotEmpty(reqInfo.getReturnFilter())){
                columnsNeed=getColumnFilter(data, reqInfo.getReturnFilter());
            }else if(needAll){
                columnsNeed=data.getColumnList();
            }
        }else{
            columnsNeed=data.getColumnList();
        }
        for (TServiceColumn column : columnsNeed) {
            if ("1".equals(column.getIsTransfer())) {
                columns[column.getColumnIntIndex()] = column;
            }
            if (orderColumnInt == -1 && orderColumnName != null && orderColumnName.equals(column.getColumnName())) {
                orderColumnInt = column.getColumnIntIndex();
            }
        }

        if (orderColumnInt != -1) {
            List<OrderRow> orderRows = new ArrayList<OrderRow>(data.getData().length);

            for (int i = 0; i < data.getData().length; i++) {
                final Object[] row = data.getData()[i];
                rowData = new LinkedHashMap<String, String>();

                for (int j = 0; j < row.length; j++) {
                    if (columns[j] == null) {
                        continue;
                    }

                    String key = columns[j].getColumnName();
                    if (!isDbLangZH) {
                        final String sqlColumnName = columns[j].getSqlColumnName();
                        if (sqlColumnName != null && sqlColumnName.trim().length() != 0) {
                            key = sqlColumnName;
                        }
                    }

                    rowData.put(key, DataUtil.getStringValue(row[j]));
                }

                orderRows.add(new OrderRow(row[orderColumnInt], rowData));
            }

            Collections.sort(orderRows);

            if ("desc".equalsIgnoreCase(order)) {
                for (int i = orderRows.size() - 1; i >= 0; i--) {
                    rows.add(orderRows.get(i).row);
                }
            } else {
                for (int i = 0, size = orderRows.size(); i < size; i++) {
                    rows.add(orderRows.get(i).row);
                }
            }
        } else {
            for (int i = 0; i < data.getData().length; i++) {
                final Object[] row = data.getData()[i];
                rowData = new LinkedHashMap<String, String>();

                for (int j = 0; j < row.length; j++) {
                	
                    if (columns[j] == null) {
                        continue;
                    }

                    String key = columns[j].getColumnName();
                    if (!isDbLangZH) {
                        final String sqlColumnName = columns[j].getSqlColumnName();
                        if (sqlColumnName != null && sqlColumnName.trim().length() != 0) {
                            key = sqlColumnName;
                        }
                    }

                    rowData.put(key, DataUtil.getStringValue(row[j]));
                }

                rows.add(rowData);
            }
        }

        return rows;
    }

    public DataSources getDataSource(String dsName, String styleSn) {
        DataSources dataSources=null;
        //从缓存数据去拿
        Map<String, DataSources>  dataSourceCacheMap=CachePool.getInstance().getDataSourceCacheMap();
        if(StringUtils.isEmpty(styleSn)){
           dataSources=dataSourceCacheMap.get(dsName);
        }
        if(dataSources!=null){
            return dataSources;
        }else{
            if (StringUtils.isEmpty(styleSn)) {
                dataSources = dataSourcesDao.findByName(dsName);
            } else {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("name", dsName);
                paramMap.put("styleNumber", styleSn);
                dataSources = dataSourcesDao.findByNameAndStyleNumber(paramMap);
            }
            if(dataSources != null && StringUtils.isEmpty(styleSn)){
                dataSourceCacheMap.put(dsName, dataSources);
            }
        }
//       if (StringUtils.isEmpty(styleSn)) {
//             dataSources = dataSourcesDao.findByName(dsName);
//         } else {
//             Map<String, String> paramMap = new HashMap<String, String>();
//             paramMap.put("name", dsName);
//             paramMap.put("styleNumber", styleSn);
//             dataSources = dataSourcesDao.findByNameAndStyleNumber(paramMap);
//         }
        return dataSources;
    }

    /**
     * 
    * @Title: getColumnFilter 
    * @Description: TODO(处理需要过滤字段，返回指定内容) 
    * @param @param data
    * @param @param columnsNeed
    * @param @param filter
    * @param @return  参数说明 
    * @return List<TServiceColumn>    返回类型 
    * @throws
     */
    public static List<TServiceColumn> getColumnFilter(D2Data data, String filter){
        List<TServiceColumn> columnsNeed = new ArrayList<TServiceColumn>();
        if(!StringUtils.isBlank(filter)){//过滤需要返回字段
            String[] column = filter.split(",");
            List<TServiceColumn> columnList = data.getColumnList();
            columnsNeed = new ArrayList<TServiceColumn>();
            for(int i=0;i<columnList.size();i++){
                for(String str:column){
                    boolean columnIsExist = columnList.get(i).getColumnName()!=null && columnList.get(i).getColumnName().equals(str);
                    boolean sqlColumnIsExist = columnList.get(i).getSqlColumnName()!=null && columnList.get(i).getSqlColumnName().equals(str);
                    if(columnIsExist||sqlColumnIsExist){
                        columnsNeed.add(columnList.get(i));
                    }
                }
            }
        }
        return columnsNeed;
    }
}

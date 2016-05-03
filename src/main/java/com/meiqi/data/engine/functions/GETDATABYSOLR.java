package com.meiqi.data.engine.functions;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4D2Data;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.Services;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.action.impl.DataActionImpl;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.po.solr.SolrServiceResponseData;
import com.meiqi.dsmanager.util.ListUtil;
import com.meiqi.dsmanager.util.SolrClientUtil;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.commons.util.Arith;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;
/**
 * 从solr获取数据
 * @author meiqidr
 *
 */
public class GETDATABYSOLR extends Function{

	public static final String NAME = GETDATABYSOLR.class.getSimpleName();
	static final Function O_GETBYPARA = getFunction("_O_GETBYPARA");
	private static final Log LOG =  LogFactory.getLog("solr");
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
	    String site_id="";
	    if(calInfo.getParam()!=null){
	        site_id=calInfo.getParam().get("site_id")==null?"0":calInfo.getParam().get("site_id").toString();
	    }
		// TODO Auto-generated method stub
		if(1>args.length){
			throw new ArgsCountError(NAME);
		}
		String param=(String) args[0];
		if(null==param){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		if(param.equals("")){
		    throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		LOG.info("query data function solr param:"+param);
		DsManageReqInfo dsReqInfo = DataUtil.parse(param, DsManageReqInfo.class);
		dsReqInfo.getParam().put("site_id", site_id);
		long defResult=0;
		if(2==args.length){
			defResult=(Long) args[1];
			if(1==defResult){
				LOG.info("data function solr set defResult=1");
				dsReqInfo.getParam().put(ISolrAction.DEF_RESULT_NAME,defResult);
			}
		}
		
//		SolrActionImpl solrActionImpl=(SolrActionImpl)MyApplicationContextUtil.getBean("solrActionImpl");
//		String resultData = solrActionImpl.query(dsReqInfo);
		String resultData =query(dsReqInfo,calInfo);
		JSONObject jsonResultData=JSONObject.parseObject(resultData);
		
		//=2获取total值
		if(2==defResult){
			return jsonResultData.getString("total");
		}else{
			return jsonResultData.getString("rows");
		}
	}
	
	
	  public String query(DsManageReqInfo reqInfo,CalInfo calInfo) {
	      String reqKey=DataUtil.toJSONString(reqInfo);
	        String istimely = reqInfo.getParam().get("istimely")==null?"":reqInfo.getParam().get("istimely").toString();;//是否要做替换数据的标示1是 2否
	        //获取自定义的缓存数据
	        if(!"1".equals(istimely)){
	            String cacheData=SolrClientUtil.solrDataCache.get(reqKey);
	            if(StringUtils.isNotEmpty(cacheData)){
	                LOG.info("solr get cache reqKey:" + reqKey);
	                return cacheData;
	            }
	        }
	        String collectionName = reqInfo.getServiceName();
	        // 重构sumInfo函数
	        long begin1 = System.currentTimeMillis();
	        Map<String,String> resultMap=new HashMap<String, String>();
	        SolrClientUtil.requestConvertForSumInfo(collectionName, DataUtil.toJSONString(reqInfo),resultMap);
	        String content=resultMap.get("convertResult");
	        long time1 = System.currentTimeMillis() - begin1;
	        LOG.info("solr requestConvertForSumInfo time:" + time1 + ",content:" + content);
	        reqInfo = DataUtil.parse(content, DsManageReqInfo.class);

	        SolrServiceResponseData solrServiceResponseData = new SolrServiceResponseData();
	        String jsonResp = null;
	        String url = "";
	        HttpSolrServer client=null;
	        try {
	            // 获取索引服务器地址
	            url = SysConfig.getValue("solr.solrServerUrl") + "/" + collectionName;
	            client = SolrClientUtil.getInstance(url);
	            Map<String, Object> params = reqInfo.getParam();

	            int dataRows = params.get("rows") == null ? 0 : Integer.valueOf(params.get("rows").toString());
	            String groupExp = "";
	            String groupSortExp = "";
	            String innerGroupSortExp="";
	            
	            Object fl = params.get("fl");
	            String flStr = "";
	            if (fl != null) {
	                flStr = fl.toString();
	            } else {
	                flStr = "*";
	            }
	            int start=0;
	            if(params.get("start")!=null && !"".equals(params.get("start").toString())){
	                start=Integer.parseInt(params.get("start").toString());
	            }
	            
	            
	            Map<String, Object> map = new HashMap<String, Object>();
	            String facetFields=params.get("facetFields")==null?"":params.get("facetFields").toString();
	            if(StringUtils.isNotEmpty(facetFields)){
	                map=SolrClientUtil.queryByGroup(client,params);
	            }else{
	                long begin = System.currentTimeMillis();
	                SolrDocumentList docs = SolrClientUtil.queryDocs(client, params);
	                long time = System.currentTimeMillis() - begin;
	                LOG.info("solr queryDocs tak time:" + time+ ",content:" + content);
	                if (ListUtil.isNullOrEmpty(docs) && params.get(ISolrAction.DEF_RESULT_NAME) != null
	                        && "1".equals(params.get(ISolrAction.DEF_RESULT_NAME).toString())) {
	                    params.remove("q");
	                    params.remove("fq");
	                    docs = SolrClientUtil.queryDocs(client, params);
	                    map.put(ISolrAction.DEF_RESULT_NAME, "1");
	                }
	                String scale="";
	                List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
	                Map<Float,Double> scaleToCount=new HashMap<Float, Double>();
	                // 分组需求
	                Object groupByObject = params.get("groupBy");
	                if (ListUtil.notEmpty(docs) && groupByObject != null) {
	                    long groupBegin = System.currentTimeMillis();
	                    String groupBy = groupByObject.toString();
	                    if (StringUtils.isNotEmpty(groupBy)) {
	                        Map<Float, SolrDocumentList> mapSolrDocs = new HashMap<Float, SolrDocumentList>();
	                        Map<String, Object> groupParam = DataUtil.parse(groupBy, Map.class);
	                        groupExp = groupParam.get("groupExp").toString();
	                        groupSortExp = groupParam.get("groupSortExp").toString();
	                        String sort = groupParam.get("sort").toString();
	                        scale=groupParam.get("scale")==null?"":groupParam.get("scale").toString();
	                        Set<Float> innerGroupSortSet=null;
	                        String innerGroupSortType="";
	                        Map<Float,Map<Float,Map<Float, List<SolrDocument>>>> groupSortToInnerGroupSortTogroupExpValueDocs =null;
	                        Set<Float> groupValues=new HashSet<Float>();
	                        int innerGroupHeight=0;
	                        if(groupParam.get("innerGroupSortExp")!=null){
	                            innerGroupSortExp=groupParam.get("innerGroupSortExp").toString();
	                            innerGroupSortSet=new HashSet<Float>();
	                            innerGroupSortType=groupParam.get("innerGroupSortType").toString();
	                            groupSortToInnerGroupSortTogroupExpValueDocs = new HashMap<Float,Map<Float,Map<Float, List<SolrDocument>>>>();//组与组内的排序值关系
	                        }
	                        Map<Float, Map<Float, List<Float>>> groupSortExpValueMappingGroupExpValue = new HashMap<Float, Map<Float, List<Float>>>();// 排序值与组关系
	                        Set<Float> groupSortExpValueSet = new HashSet<Float>();
	                        Map<Float,Double> numberOfgroupExpValue=new HashMap<Float, Double>();
	                        for (SolrDocument doc : docs) {
	                            Float groupExpValue = Float.valueOf(doc.get(groupExp).toString());
	                            Double currentNumber=numberOfgroupExpValue.get(groupExpValue);
	                            if(currentNumber!=null){
	                                numberOfgroupExpValue.put(groupExpValue, currentNumber+1);
	                            }else{
	                                numberOfgroupExpValue.put(groupExpValue, (double) 1);
	                            }
	                            groupValues.add(groupExpValue);
	                            Float groupSortExpValue = Float.valueOf(doc.get(groupSortExp).toString());
	                            Float innerGroupSortExpValue=null;
	                            if(innerGroupSortSet!=null){
	                                innerGroupSortExpValue=Float.valueOf(doc.get(innerGroupSortExp).toString());
	                                innerGroupSortSet.add(innerGroupSortExpValue);
	                                Map<Float,Map<Float, List<SolrDocument>>> innerGroupSortTogroupExpValueDocs=groupSortToInnerGroupSortTogroupExpValueDocs.get(groupSortExpValue);
	                                if(innerGroupSortTogroupExpValueDocs == null){
	                                    innerGroupSortTogroupExpValueDocs=new HashMap<Float,Map<Float, List<SolrDocument>>>();
	                                }
	                                Map<Float, List<SolrDocument>> groupToDocs=innerGroupSortTogroupExpValueDocs.get(innerGroupSortExpValue);
	                                if(groupToDocs==null){
	                                    groupToDocs=new HashMap<Float, List<SolrDocument>>();
	                                }
	                                List<SolrDocument> ds=groupToDocs.get(groupExpValue);
	                                if(ds==null){
	                                    ds=new ArrayList<SolrDocument>();
	                                }
	                                ds.add(doc);
	                                groupToDocs.put(groupExpValue, ds);
	                                innerGroupSortTogroupExpValueDocs.put(innerGroupSortExpValue, groupToDocs);
	                                
	                                if(ds.size()>innerGroupHeight){
	                                    innerGroupHeight= ds.size();
	                                }
	                                groupSortToInnerGroupSortTogroupExpValueDocs.put(groupSortExpValue, innerGroupSortTogroupExpValueDocs);
	                            }
	                            Map<Float, List<Float>> groupExpValues = groupSortExpValueMappingGroupExpValue
	                                    .get(groupSortExpValue);
	                            if (groupExpValues == null) {
	                                groupExpValues = new HashMap<Float, List<Float>>();
	                                List<Float> groupExpValuesTmp = new ArrayList<Float>();
	                                groupExpValuesTmp.add(groupExpValue);
	                                groupExpValues.put(groupExpValue, groupExpValuesTmp);
	                            } else {
	                                if (!groupExpValues.keySet().contains(groupExpValue)) {
	                                    List<Float> groupExpValuesTmp = new ArrayList<Float>();
	                                    groupExpValuesTmp.add(groupExpValue);
	                                    groupExpValues.put(groupExpValue, groupExpValuesTmp);
	                                } else {
	                                    List<Float> groupExpValuesTmp = groupExpValues.get(groupExpValue);
	                                    groupExpValuesTmp.add(groupExpValue);
	                                    groupExpValues.put(groupExpValue, groupExpValuesTmp);
	                                }
	                            }
	                            groupSortExpValueMappingGroupExpValue.put(groupSortExpValue, groupExpValues);
	                            groupSortExpValueSet.add(Float.valueOf(groupSortExpValue));
	                            SolrDocumentList currentSolrDocList = mapSolrDocs.get(groupExpValue);
	                            if (currentSolrDocList == null) {
	                                currentSolrDocList = new SolrDocumentList();
	                            }
	                            currentSolrDocList.add(doc);
	                            mapSolrDocs.put(groupExpValue, currentSolrDocList);
	                        }
	                        List<Float> groupSortExpValueList = new ArrayList<Float>();
	                        for (Float key : groupSortExpValueSet) {
	                            groupSortExpValueList.add(key);
	                        }
	                        // 安装组的排序重新输出solr数据顺序
	                        if ("asc".equals(sort)) {
	                            Collections.sort(groupSortExpValueList);
	                        } else {
	                            Collections.sort(groupSortExpValueList, new Comparator<Float>() {
	                                @Override
	                                public int compare(Float o1, Float o2) {
	                                    return (int) (o2 - o1);
	                                }
	                            });
	                        }
	                        int size=docs.size();
	                        List<Float> innerGroupSortValueList = new ArrayList<Float>();
	                        docs.clear();
	                        int startFlag=0;
	                        if(!CollectionsUtils.isNull(innerGroupSortSet)){
	                            for (Float key : innerGroupSortSet) {
	                                innerGroupSortValueList.add(key);
	                            }
	                            if ("asc".equals(innerGroupSortType)) {
	                                Collections.sort(innerGroupSortValueList);
	                            } else {
	                                Collections.sort(innerGroupSortValueList, new Comparator<Float>() {
	                                    @Override
	                                    public int compare(Float o1, Float o2) {
	                                        return (int) (o2 - o1);
	                                    }
	                                });
	                            }
	                            String[] scales=scale.split(":");
	                            if(StringUtils.isEmpty(scale) || scales.length!=groupValues.size()){
	                                LOG.info("solr 当前组数:"+groupValues.size()+",传入的比列为："+scales);
	                                while (docs.size() < dataRows && docs.size() != size &&  startFlag!=size) {
	                                    for(Float innerSort:innerGroupSortValueList){
	                                        if (docs.size() == dataRows || docs.size()== size || startFlag==size) {
	                                            break;
	                                        }
	                                        for(int i=0;i<innerGroupHeight;i++){
	                                            if (docs.size() == dataRows || docs.size()== size || startFlag==size) { 
	                                                break;
	                                            }
	                                            for (Float key : groupSortExpValueList) {
	                                                if (docs.size() == dataRows || docs.size()== size || startFlag==size) { 
	                                                    break;
	                                                }
	                                                Map<Float,Map<Float, List<SolrDocument>>> innerGroupSortTogroupExpValueDocs=groupSortToInnerGroupSortTogroupExpValueDocs.get(key);
	                                                if(!CollectionsUtils.isNull(innerGroupSortTogroupExpValueDocs)){
	                                                    Map<Float, List<SolrDocument>> groupExpValueDocs=innerGroupSortTogroupExpValueDocs.get(innerSort);
	                                                    if(!CollectionsUtils.isNull(groupExpValueDocs)){
	                                                        Set<Float> set=groupExpValueDocs.keySet();
	                                                        Iterator<Float> is=set.iterator();
	                                                        while(is.hasNext()){
	                                                            Float f=is.next();
	                                                            if (docs.size() == dataRows || docs.size()== size || startFlag==size) { 
	                                                                break;
	                                                            }
	                                                            List<SolrDocument> tmpList=groupExpValueDocs.get(f);
	                                                            if(!CollectionsUtils.isNull(tmpList)){
	                                                                SolrDocument doc=tmpList.remove(0);
	                                                                if(startFlag>=start){
	                                                                    docs.add(doc);
	                                                                }
	                                                                startFlag++;
	                                                            }
	                                                        }
	                                                    }
	                                                }
	                                            }
	                                        }
	                                    }
	                                }
	                            }else{
	                                    Double totalScale=(double) 0;
	                                    for(String t:scales){
	                                        totalScale+=Double.parseDouble(t);
	                                    }
	                                    LOG.info("solr 每组对应的数目："+groupSortExpValueMappingGroupExpValue);
	                                    List<Float> sortedGroupValues=new ArrayList<Float>();
	                                    for(int i=0;i<groupSortExpValueList.size();i++){
	                                        Float f=groupSortExpValueList.get(i);
	                                        Map<Float, List<Float>> m=groupSortExpValueMappingGroupExpValue.get(f);
	                                        if(m!=null){ 
	                                            Map<Float, List<Float>> mm=groupSortExpValueMappingGroupExpValue.get(f);
	                                            if(mm!=null){
	                                                Set<Float> groupValuesTmp=mm.keySet();
	                                                for(Float  groupValue:groupValuesTmp){
	                                                    sortedGroupValues.add(groupValue);
	                                                }
	                                            }
	                                        }
	                                    }
	                                    Double total=(double) 0;
	                                    Map<Float,Double> eacheScaleToCount=new HashMap<Float, Double>();
	                                    for(int j=0;j<sortedGroupValues.size();j++){
	                                        Double number=Arith.ceiling(Arith.div(Arith.mul(dataRows,Double.parseDouble(scales[j])), totalScale),0);
	                                        Float gv=sortedGroupValues.get(j);
	                                        Double nn=numberOfgroupExpValue.get(gv);
	                                        if(number>nn){
	                                            total+=nn;
	                                            scaleToCount.put(gv, nn.doubleValue());
	                                        }else{
	                                            total+=number;
	                                            scaleToCount.put(gv, number);
	                                        }
	                                        eacheScaleToCount.put(gv,Double.parseDouble(scales[j]));
	                                    }
	                                    
	                                    Map<Float,List<SolrDocument>>  lastGroupValueToDocs=new HashMap<Float, List<SolrDocument>>();
	                                    Map<Float,List<SolrDocument>>  currentGroupValueToDocs=new HashMap<Float, List<SolrDocument>>();
	                                    while (docs.size() < dataRows && docs.size() != size &&  startFlag!=size && docs.size() !=total) {
	                                     for (Float key : groupSortExpValueList) {
	                                                currentGroupValueToDocs.clear();
	                                                if (docs.size() == dataRows || docs.size()== size || startFlag==size || docs.size() ==total) { 
	                                                    break;
	                                                }
	                                                Map<Float,Map<Float, List<SolrDocument>>> innerGroupSortTogroupExpValueDocs=groupSortToInnerGroupSortTogroupExpValueDocs.get(key);
	                                                boolean flag=false;
	                                                for(Float innerSort:innerGroupSortValueList){
	                                                    if (docs.size() == dataRows || docs.size()== size || startFlag==size || docs.size() ==total) {
	                                                        break;
	                                                    }
	                                                    if(flag){
	                                                        break;
	                                                    }
	                                                    if(!CollectionsUtils.isNull(innerGroupSortTogroupExpValueDocs)){
	                                                             Map<Float, List<SolrDocument>> groupExpValueDocs=innerGroupSortTogroupExpValueDocs.get(innerSort);
	                                                             if(groupExpValueDocs!=null && groupExpValueDocs.size()!=0){
	                                                                         Set<Float> f=groupExpValueDocs.keySet();
	                                                                         for(Float groupValue:f){
	                                                                                 List<SolrDocument> tmpList=groupExpValueDocs.get(groupValue);
	                                                                                 Iterator<SolrDocument> iterator=tmpList.iterator();
	                                                                                 while(iterator.hasNext()){
	                                                                                     SolrDocument doc=iterator.next();
	                                                                                     if(startFlag>=start){
	                                                                                         Double rightNumber=scaleToCount.get(groupValue);
	                                                                                         Double currentRightNumber=eacheScaleToCount.get(groupValue);
	                                                                                         List<SolrDocument> ds=lastGroupValueToDocs.get(groupValue);
	                                                                                         List<SolrDocument> currentDs=currentGroupValueToDocs.get(groupValue);
	                                                                                         if(ds==null){
	                                                                                              ds=new ArrayList<SolrDocument>();
	                                                                                         }
	                                                                                         if(currentDs==null){
	                                                                                             currentDs=new ArrayList<SolrDocument>();
	                                                                                         }
	                                                                                         if(currentDs.size()==currentRightNumber || ds.size()==numberOfgroupExpValue.get(groupValue) || docs.size() ==total){
	                                                                                             //如果当前组取够了数目，则不取了
	                                                                                             flag=true;
	                                                                                             break;
	                                                                                         }
	                                                                                         if(ds.size()==rightNumber || ds.size()==numberOfgroupExpValue.get(groupValue) || docs.size() ==total){
	                                                                                             //如果当前组取够了数目，则不取了
	                                                                                             flag=true;
	                                                                                             break;
	                                                                                         }
	                                                                                         ds.add(doc);
	                                                                                         currentDs.add(doc);
	                                                                                         currentGroupValueToDocs.put(groupValue, currentDs);
	                                                                                         lastGroupValueToDocs.put(groupValue, ds);
	                                                                                         docs.add(doc);
	                                                                                         iterator.remove();
	                                                                                     }
	                                                                                     startFlag++;
	                                                                                 }
	                                                                         }
	                                                             }
	                                                     }
	                                                }
	                                        }
	                                    }
	                            }
	                        }else{
	                            while (docs.size() < dataRows && docs.size() != size &&  startFlag!=size) {
	                                for (Float key : groupSortExpValueList) {
	                                    // 最多dataRows组（算法优化）
	                                    if (docs.size() == dataRows || docs.size()== size || startFlag==size) { 
	                                        break;
	                                    }
	                                    Map<Float, List<Float>> tmpMap = groupSortExpValueMappingGroupExpValue.get(key);
	                                    for (Float key1 : tmpMap.keySet()) {
	                                        // 最多dataRows组（算法优化）
	                                        if (docs.size() == dataRows || docs.size()== size || startFlag==size) { 
	                                            break;
	                                        }
	                                        List<Float> strs = tmpMap.get(key1);
	                                        if(ListUtil.notEmpty(strs)){
	                                            Float str = strs.remove(0);
	                                            SolrDocumentList currentSolrDocList = mapSolrDocs.get(str);
	                                            if (ListUtil.notEmpty(currentSolrDocList)) {
	                                                SolrDocument doc = currentSolrDocList.remove(0);
	                                                if(startFlag>=start){
	                                                    if (doc != null) {
	                                                         docs.add(doc);
	                                                         break;
	                                                    }
	                                               }
	                                               startFlag++;
	                                            }
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    long groupTime = System.currentTimeMillis() - groupBegin;
	                    LOG.info("solr 分组耗时：" + groupTime + "毫秒");
	                }
	                
	                long begin4=System.currentTimeMillis();
	                // 设置是否需要输出groupExp，groupSortExp标示
	                boolean outputGroupExpFlag = false;// 默认不输出
	                boolean outputGroupSortExpFlag = false;// 默认不输出
	                boolean innerGroupSortExpFlag = false;// 默认不输出
	                if (groupByObject != null && !"".equals(groupByObject.toString().trim()) && !"".equals(flStr)) {
	                    if (!"".equals(groupExp) && flStr.contains(groupExp)) {
	                        outputGroupExpFlag = true;
	                    }
	                    if (!"".equals(groupSortExp) && flStr.contains(groupSortExp)) {
	                        outputGroupSortExpFlag = true;
	                    }
	                    if (!"".equals(innerGroupSortExp) && flStr.contains(innerGroupSortExp)) {
	                        innerGroupSortExpFlag = true;
	                    }
	                }
	                if (ListUtil.notEmpty(docs)) {
	                    Map<String, Map<String, String>> mapss = new HashMap<String, Map<String, String>>();
	                    if ("1".equals(istimely)) {
	                        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
	                        dsReqInfo.setServiceName("solr_index_info");
	                        Map<String, Object> param = new HashMap<String, Object>();
	                        param.put("index_name", collectionName);
	                        param.put("site_id", params.get("site_id"));
	                        dsReqInfo.setParam(param);
	                        String data = getData(dsReqInfo,calInfo);
	                        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
	                        List<Map<String, String>> lits = responseData.getRows();
	                        if (lits != null && lits.size()>0) {
	                            Map<String, String> maps = lits.get(0);
	                                String ruleName = maps.get("rule_name");
	                                String input_key = maps.get("input_key");
	                                String out_key = maps.get("out_key");
	                                DsManageReqInfo dsReqInfo1 = new DsManageReqInfo();
	                                dsReqInfo1.setServiceName(ruleName);
	                                Map<String, Object> param1 = new HashMap<String, Object>();
	                                if (StringUtils.isNotEmpty(input_key)) {
	                                    String[] inputKeyArray = input_key.split(",");
	                                    for (SolrDocument doc : docs) {
	                                        for (String inputKey : inputKeyArray) {
	                                            Object obj=doc.get(inputKey);
	                                            if(obj!=null){
	                                                String inputKeyValue = obj.toString();
	                                                Object values = param1.get(inputKey);
	                                                if (values == null) {
	                                                    values = inputKeyValue;
	                                                } else {
	                                                    values = values.toString() + "," + inputKeyValue;
	                                                }
	                                                param1.put(inputKey, values);
	                                            }
	                                        }
	                                    }
	                                }
	                                if (StringUtils.isNotEmpty(out_key)) {
	                                    String[] outKeyArray = out_key.split(",");
	                                    for (String outputKey : outKeyArray) {
	                                        if (params.get(outputKey) != null && !"".equals(params.get(outputKey).toString())) {
	                                            param1.put(outputKey, params.get(outputKey));
	                                        }
	                                    }
	                                }
	                                param1.put("site_id", params.get("site_id"));
	                                dsReqInfo1.setParam(param1);
	                                long begin3=System.currentTimeMillis();
	                                String data1 = getData(dsReqInfo1,calInfo);
	                                long time3=System.currentTimeMillis()-begin3;
	                                LOG.info("solr replace get rule data time:"+time3+",param:"+JSONObject.toJSONString(dsReqInfo1));
	                                RuleServiceResponseData responseData1 = DataUtil.parse(data1, RuleServiceResponseData.class);
	                                List<Map<String, String>> lits1 = responseData1.getRows();
	                                if (lits1 != null && lits1.size() > 0) {
	                                    for (Map<String, String> m : lits1) {
	                                        String solrIdName=m.get("solrid");
	                                        String solrIdValue="";
	                                        if(StringUtils.isNotEmpty(solrIdName)){
	                                            solrIdValue=m.get(solrIdName);
	                                        }else{
	                                            solrIdValue=m.get("id");
	                                        }
	                                        if (StringUtils.isNotEmpty(solrIdValue)){
	                                            mapss.put(solrIdValue, m);
	                                        }
	                                    }
	                                }
	                        }
	                    }
	                    if ("1".equals(istimely) && mapss.size() > 0) {
	                        for (SolrDocument doc : docs) {
	                            Map<String, Object> map1 = new HashMap<String, Object>();
	                            Set<String> keys = doc.keySet();
	                            String id = doc.get("id").toString();
	                            for (String key : keys) {
	                                map1.put(key, doc.get(key));
	                            }
	                            if (mapss.size() > 0) {
	                                Map<String, String> tmp = mapss.get(id);
	                                if(tmp!=null){
	                                    String solrIdName=tmp.get("solrid");
	                                    if(StringUtils.isNotEmpty(solrIdName)){
	                                        solrIdName="id";
	                                    }
	                                    for (String k : tmp.keySet()) {
	                                        if (!"id".equals(k) && !"solrid".equals(k) && !k.equals(solrIdName)) {
	                                            if(map1.get(k)!=null){
	                                              map1.put(k, tmp.get(k));
	                                            }
	                                        }
	                                        if (groupExp.equals(k) && !outputGroupExpFlag) {
	                                            map1.remove(groupExp);
	                                        }
	                                        if (groupSortExp.equals(k) && !outputGroupSortExpFlag) {
	                                            map1.remove(groupSortExp);
	                                        }
	                                        if (innerGroupSortExp.equals(k) && !innerGroupSortExpFlag) {
	                                            map1.remove(groupSortExp);
	                                        }
	                                    }
	                                }
	                            }
	                            rows.add(map1);
	                        }
	                    } else {
	                        for (SolrDocument doc : docs) {
	                            Map<String, Object> map1 = new HashMap<String, Object>();
	                            Set<String> keys = doc.keySet();
	                            for (String key : keys) {
	                                map1.put(key, doc.get(key));
	                                if (groupExp.equals(key) && !outputGroupExpFlag) {
	                                    map1.remove(groupExp);
	                                }
	                                if (groupSortExp.equals(key) && !outputGroupSortExpFlag) {
	                                    map1.remove(groupSortExp);
	                                }
	                            }
	                            rows.add(map1);
	                        }
	                    }
	                    map.put("rows", rows);
	                }
	                long time4=System.currentTimeMillis()-begin4;
	                LOG.info("build solr data time:"+time4);
	                map.put("total", docs.getNumFound());
	            }
	            long begin5=System.currentTimeMillis();
	            List<Map<String, Object>>  rows=(List<Map<String, Object>>)map.get("rows");
	            if (ListUtil.notEmpty(rows)) {
	                Object t=map.get("total")==null?"0":map.get("total");
	                int total=Integer.parseInt(t.toString());
	                solrServiceResponseData.setTotal(total);
	                solrServiceResponseData.setRows(rows);
	                if(map.get(ISolrAction.DEF_RESULT_NAME)!=null){
	                    solrServiceResponseData.setDefResult(map.get(ISolrAction.DEF_RESULT_NAME).toString());
	                }
	                solrServiceResponseData.setCode(DsResponseCodeData.SUCCESS.code);
	                solrServiceResponseData.setDescription(DsResponseCodeData.SUCCESS.description);
	            } else {
	                List<Map<String, Object>>  rsTmp=new ArrayList<Map<String, Object>>();
	                solrServiceResponseData.setTotal(0);
	                solrServiceResponseData.setRows(rsTmp);
	                if(map.get(ISolrAction.DEF_RESULT_NAME)!=null){
	                    solrServiceResponseData.setDefResult(map.get(ISolrAction.DEF_RESULT_NAME).toString());
	                }
	                solrServiceResponseData.setCode(DsResponseCodeData.NO_DATA.code);
	                solrServiceResponseData.setDescription(DsResponseCodeData.NO_DATA.description);
	            }
	            long time5=System.currentTimeMillis()-begin5;
	            LOG.info("convert solr data time:"+time5);
	        } catch (Exception e) {
	            e.printStackTrace();
	            String error="search error:\r\nServer Address:" + url + "\r\nJson to server:\r\n" + reqInfo.getParam()+ "\r\n:Json from server:\r\n:" + jsonResp+",error:"+e;
	            LOG.error(error);
	            solrServiceResponseData = new SolrServiceResponseData();
	            solrServiceResponseData.setCode(DsResponseCodeData.ERROR.code);
	            solrServiceResponseData.setDescription(error);
	            List<Map<String, Object>>  rs=new ArrayList<Map<String, Object>>();
	            solrServiceResponseData.setRows(rs);
	        }
	        String resultData = JSON.toJSONString(solrServiceResponseData);
	        if(!"1".equals(istimely) && ListUtil.notEmpty(solrServiceResponseData.getRows())){
	            //如果有数据而且不需要实时替换的，那么就放入缓存中
	            LOG.info("solr set cache reqKey:" + reqKey);
	            synchronized (SolrClientUtil.solrDataCache) {
	                SolrClientUtil.solrDataCache.put(reqKey, resultData);
	              }
	        }
	        return resultData;
	    }
	
	private String getData(DsManageReqInfo dsReqInfo,CalInfo calInfo) throws Exception{
		TService servicePo = Services.getService(dsReqInfo.getServiceName());
		Map<String, Object> currentParam=dsReqInfo.getParam();
		
		
		final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, currentParam, calInfo.getCallLayer()
                        , calInfo.getServicePo(), calInfo.getParam(), NAME);
		
        boolean isBaseService=servicePo.getBaseServiceID()==null?true:false;
        boolean needAll = "1".equals(dsReqInfo.getNeedAll());
        boolean isDbLangZH = true;
        if (dsReqInfo.getDbLang() != null) {
            isDbLangZH = dsReqInfo.getDbLang().trim().equalsIgnoreCase("zh");
        }
        RuleServiceResponseData respInfo = new RuleServiceResponseData();
        respInfo.setRows(DataActionImpl.data2Rows(isBaseService,d2Data, servicePo.getColumns(), needAll,dsReqInfo, isDbLangZH, "", ""));
        return JSON.toJSONString(respInfo);
	}
}

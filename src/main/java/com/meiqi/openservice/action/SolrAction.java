/**   
* @Title: RegisterAction.java 
* @Package com.meiqi.openservice.action.register 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月8日 上午11:02:08 
* @version V1.0   
*/
package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.ListUtil;
import com.meiqi.app.pay.alipay.sign.Base64;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.SolrClientUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.SolrUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;
import com.meiqi.util.MyApplicationContextUtil;

/** 
 * @ClassName: SolrAction 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08 
 *  
 */
@Service
public class SolrAction extends BaseAction{

	@Autowired
    private IDataAction dataAction;
	
	@Autowired
    private ISolrAction solrAction;

    @Autowired
    private EtagService eTagService;
    
    private static final Log LOG =  LogFactory.getLog("solr");
    
    @Autowired
    private ThreadHelper  indexTheadHelper;
    
    
    public String clearFencis(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        SolrClientUtil.clearFencis();
        LOG.info("solr clearFencis");
        return "success";
    }
    
    public String clearSolrCacheData(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        SolrClientUtil.clearsolrDataCache();
        LOG.info("solr clearSolrCacheData");
        return "success";
    }
    
	public String queryByConvert(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
		
	    ResponseInfo respInfo=new ResponseInfo();

	    long begin=System.currentTimeMillis();
	    Map<String,Object> param = DataUtil.parse(repInfo.getParam(), Map.class);
	    String site_id=param.get("site_id")==null?"0":param.get("site_id").toString();

		DsManageReqInfo dsReqInfoRule1=new DsManageReqInfo();
        String serviceName1="SI_Argument";
        dsReqInfoRule1.setServiceName(serviceName1);
        dsReqInfoRule1.setNeedAll("1");
        Map<String,Object> param1=new HashMap<String, Object>();
        param1.put("site_id", site_id);
        dsReqInfoRule1.setParam(param1);
        String resultData11 = dataAction.getData(dsReqInfoRule1,"");
        RuleServiceResponseData responseData1 = null;
        responseData1 = DataUtil.parse(resultData11, RuleServiceResponseData.class);
        List<Map<String, String>> list1=responseData1.getRows();
        LOG.info("query solr SI_Argument param:"+list1.toString());
        String serviceName=""; 
		for(Map<String, String> map:list1){
		    if((map.get("url_type").equals(param.get("url_type").toString()))){
		        serviceName=map.get("argument_rule");
		        break;
		    }
		}
		if(StringUtils.isEmpty(serviceName)){
		    respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(param.get("url_type").toString()+" is not exist data:"+list1.toString());
            respInfo.setRows(new ArrayList<Map<String,String>>());
            return JSONObject.toJSONString(respInfo);
		}
		DsManageReqInfo dsReqInfoRule=new DsManageReqInfo();
		dsReqInfoRule.setServiceName(serviceName);
		dsReqInfoRule.setNeedAll("1");
		dsReqInfoRule.setParam(param);
	    String resultData = dataAction.getData(dsReqInfoRule,"");
	    RuleServiceResponseData responseData = null;
        responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        List<Map<String, String>> list=responseData.getRows();
        if(ListUtil.isNullOrEmpty(list)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(responseData.getDescription()+serviceName);
            respInfo.setRows(new ArrayList<Map<String,String>>());
            return JSONObject.toJSONString(respInfo);
        }
        Map<String, String> result=list.get(0);
        if(result==null){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(responseData.getDescription()+serviceName);
            respInfo.setRows(new ArrayList<Map<String,String>>());
            return JSONObject.toJSONString(respInfo);
        }
        String content1=result.get("solr_param_info");
        if(StringUtils.isEmpty(content1)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(responseData.getDescription()+serviceName);
            respInfo.setRows(new ArrayList<Map<String,String>>());
            return JSONObject.toJSONString(respInfo);
        }
        //通过solr去拿数据
        LOG.info("query solr param:"+content1);
        long time=System.currentTimeMillis()-begin;
        LOG.info("solr call rule time:"+time+",param:"+content1);

        DsManageReqInfo dsReqInfo1 = DataUtil.parse(content1, DsManageReqInfo.class);
        dsReqInfo1.getParam().putAll(param);
        dsReqInfo1.getParam().put("site_id",site_id);

        long begin1=System.currentTimeMillis();
        String resultData1 = solrAction.query(dsReqInfo1);
        // 304缓存 为app
        if (BaseAction.isFromApp(request)) {
            StringBuffer key = new StringBuffer();
            key.append(BaseAction.getPlatString(request)).append(BaseAction.validationAuthorization(request))
                    .append(Base64.encode(resultData.getBytes()));
            // 支持 2951 添加304缓存
            boolean cacheResult = eTagService.toUpdatEtag1(request, response, key.toString(), resultData1);
            if (cacheResult) {
                return null;
            }
        }
        long time1=System.currentTimeMillis()-begin1;
        LOG.info("solr query time:"+time1+",param:"+content1);
        
        long allTime=System.currentTimeMillis()-begin;
        LOG.info("solr all  query time:"+allTime+",param:"+content1);
        return resultData1;
	}
	
	/**
     * 
     * 获取中文分词结果
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String getChinaWordsResults(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.info("Function:getChinaWordsResults.Start.");
        String contentInfo = repInfo.getParam();
        if (StringUtils.isEmpty(contentInfo)) {
            return "请求参数为空";
        }
        LOG.info("获取中文分词,请求参数为：" + contentInfo);
        Map<String, Object> param = DataUtil.parse(repInfo.getParam(), Map.class);
        String result = SolrUtil.getChinaWordsResults(param);
        LOG.info("Function:getChinaWordsResults.End.");
        return result;
    }
    
    
    /**
     * 跟新solr数据
    * @Title: refreshSolrData 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return Object    返回类型 
    * @throws
     */
//    public Object refreshSolrData(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
//        
//        // RSA授权认证
//        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
//        if (!rsaVerify) {
//            ResponseInfo respInfo = new ResponseInfo();
//            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
//            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
//            return respInfo;
//        }else{
//            RsaKeyTools.doRSA(request, response,repInfo);
//        }
//        
//        ResponseInfo respInfo=new ResponseInfo();
//        String contentInfo = repInfo.getParam();
//        if (StringUtils.isEmpty(contentInfo)) {
//            return "请求参数为空";
//        }
//        String paramStr=repInfo.getParam();
//        List<String> errors=new ArrayList<String>();
//        try {
//            List<Map<String, Object>> list= DataUtil.parse(paramStr, List.class);
//            if(ListUtil.notEmpty(list)){
//                for(Map<String, Object> param:list){
//                    String collectionName=param.get("collectionName")==null?"":param.get("collectionName").toString();
//                    String id=param.get("id")==null?"":param.get("id").toString();
//                    String refreshFieldName=param.get("refreshFieldName")==null?"":param.get("refreshFieldName").toString();
//                    String refreshFieldValue=param.get("refreshFieldValue")==null?"":param.get("refreshFieldValue").toString();
//                    Map<String,String> result=SolrClientUtil.updateDoc(collectionName, id, refreshFieldName, refreshFieldValue);
//                    String r=result.get("result");
//                    if("fail".equals(r)){
//                        errors.add(result.get("error"));
//                    }
//                }
//            }else{
//                respInfo.setCode(DsResponseCodeData.ERROR.code);
//                respInfo.setDescription("refreshSolrData 传入的报文不对，没有要操作的数据"); 
//                return respInfo;
//            }
//        } catch (Exception e) {
//            respInfo.setCode(DsResponseCodeData.ERROR.code);
//            respInfo.setDescription("refreshSolrData error:"+e); 
//            return respInfo;
//        }
//        if(ListUtil.notEmpty(errors)){
//            respInfo.setCode(DsResponseCodeData.ERROR.code);
//            respInfo.setDescription(JSONObject.toJSONString(errors)); 
//        }else{
//            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
//            respInfo.setDescription(DsResponseCodeData.SUCCESS.code); 
//        }
//        return respInfo;
//    }
    
    /**
     * 更新solr数据  type(1=更新app_goods_solr的total_ready_num（有货字段）)
    * @Title: solrDataUpdate 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public void solrDataUpdateForType1(){
        solrDataUpdateForType("1");
    }
    
    /**
     * 更新solr数据  type
    * @Title: solrDataUpdate 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public void solrDataUpdateForType(String solrUpdateType){
            long begin=System.currentTimeMillis();
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName("SOLR_HSV1_Update");
            Map<String,Object> param=new HashMap<String, Object>();
            param.put("type", solrUpdateType);
            serviceReqInfo.setParam(param);
            serviceReqInfo.setNeedAll("1");
            RuleServiceResponseData responseData = null;
            dataAction=(IDataAction) MyApplicationContextUtil.getBean("dataAction");
            indexTheadHelper=(ThreadHelper)MyApplicationContextUtil.getBean("indexTheadHelper");
            String data =dataAction.getData(serviceReqInfo,"");
            responseData = DataUtil.parse(data, RuleServiceResponseData.class);
            if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                List<Map<String, String>> list=responseData.getRows();
                if(ListUtil.notEmpty(list)){
                    Map<String,String> map=list.get(0);
                    String is_success=map.get("is_success");
                    if("1".equals(is_success)){
                        String collectionName=map.get("solr_table");
                        String field_values=map.get("field_values");
                        if(StringUtils.isNotEmpty(field_values)){
                            String url = SysConfig.getValue("solr.solrServerUrl") + "/" + collectionName;
                            HttpSolrServer client = SolrClientUtil.getInstance(url);
                            List<Map<String,String>> ls=DataUtil.parse(field_values,List.class);
                            if(ListUtil.notEmpty(ls)){
                                int total=ls.size();
                                List<SolrInputDocument> docs=new ArrayList<SolrInputDocument>();
                                int size=Integer.parseInt(SysConfig.getValue("solr.betchDealSize"));
                                int i=0;
                                for(Map<String,String> m:ls){
                                    String id=m.get("id");
                                    if(StringUtils.isNotEmpty(id)){
                                        SolrDocumentList ds;
                                        try {
                                            ds = SolrClientUtil.queryDocs(client, "id:"+id);
                                            if (ListUtil.notEmpty(ds)){
                                                SolrDocument doc=ds.get(0);
                                                Collection<String> fieldNames=doc.getFieldNames();
                                                SolrInputDocument sidoc=new SolrInputDocument();
                                                for(String fieldName:fieldNames){
                                                    sidoc.addField(fieldName, doc.getFieldValue(fieldName));
                                                }
                                                for(String key:m.keySet()){
                                                    sidoc.setField(key, m.get(key));
                                                }
                                                sidoc.remove("_version_");
                                                docs.add(sidoc);
                                            }
                                            if(i!=0&&((i+1)%size==0)){
                                                //使用多线程做业务
                                                if(ListUtil.notEmpty(docs)){
                                                    BatBuildIndexThread thread=new BatBuildIndexThread(client,docs);
                                                    indexTheadHelper.execute(thread);
                                                    docs=new ArrayList<SolrInputDocument>();
                                                    Thread.sleep(5000);
                                                }
                                            }
                                            if(i==total-1){
                                                    //使用多线程做业务
                                                if(ListUtil.notEmpty(docs)){
                                                    BatBuildIndexThread thread=new BatBuildIndexThread(client,docs);
                                                    indexTheadHelper.execute(thread);
                                                    docs=new ArrayList<SolrInputDocument>();
                                                    Thread.sleep(5000);
                                                }
                                            }
                                        } catch (Exception e) {
                                            LOG.info("solrDataUpdate error:"+e);
                                        }
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
            long time=System.currentTimeMillis()-begin;
            LOG.info("solrDataUpdate type=1 time:"+time);
    }
    
    class BatBuildIndexThread implements ThreadCallback {
        private List<SolrInputDocument> docs;
        private HttpSolrServer client;
        public BatBuildIndexThread(HttpSolrServer client,List<SolrInputDocument> docs) {
            super();
            this.docs=docs;
            this.client=client;
        }

        @Override
        public void run() {
            LOG.info("request solr server url:"+client.getBaseURL()+",size:"+docs.size());
            SolrClientUtil.addDocs(client, docs);
            docs.clear();
            docs=null;
        }
    }
}

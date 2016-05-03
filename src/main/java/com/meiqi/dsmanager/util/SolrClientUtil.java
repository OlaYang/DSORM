package com.meiqi.dsmanager.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.SolrParams;

import com.meiqi.app.common.utils.ListUtil;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.HttpExecutor;
import com.meiqi.openservice.commons.util.SolrUtil;
import com.meiqi.openservice.commons.util.StringUtils;


public class SolrClientUtil {
    private static final Log LOG =  LogFactory.getLog("solr");
    private static Map<String, HttpSolrServer> map = new HashMap<String, HttpSolrServer>(1000);
    static Pattern rangePattern = Pattern.compile("^\\[\\s*([\\*|\\d])+(\\s)+TO(\\s)+([\\*|(\\d)])+\\s*\\]$");
    private static final String URL_ENCODE = "utf-8";
    private static final String wt = "json";
    public static Map<String, String[]> fencis=new LRUMap<String, String[]>(100000); 
    
    public static Map<String, String> solrDataCache=new LRUMap<String,String>(100000);
    
    private SolrClientUtil() {
    }

    
    public static void clearFencis(){
        fencis.clear();
    }
    
    public static void clearsolrDataCache(){
        solrDataCache.clear();
    }
    
    public static HttpSolrServer getInstance(String solrServerUrl) {
        HttpSolrServer client=map.get(solrServerUrl);
        if(client!=null){
            return client;
        }else{
            client = new HttpSolrServer(solrServerUrl);
            client.setSoTimeout(10000);
            client.setConnectionTimeout(10000);
            client.setDefaultMaxConnectionsPerHost(50000);
            client.setMaxTotalConnections(50000);
            client.setFollowRedirects(false);
            client.setAllowCompression(true);
            map.put(solrServerUrl, client);
            return client;
        }
//        HttpSolrServer client = new HttpSolrServer(solrServerUrl);
//        client.setSoTimeout(10000);
//        client.setConnectionTimeout(10000);
//        client.setDefaultMaxConnectionsPerHost(30000);
//        client.setMaxTotalConnections(30000);
//        client.setFollowRedirects(false);
//        client.setAllowCompression(true);
//        return client;
    }
    
    public static SolrDocumentList queryDocs(HttpSolrServer client, String query) throws IOException {
        SolrParams params = new SolrQuery(query);
        SolrDocumentList list = null;
        try {
            QueryResponse response = client.query(params);
            list = response.getResults();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Solr字符转义处理，否则查询下列字符会报查询错误。
     * 特殊字符串：+ – && || ! ( ) { } [ ] ^ ” ~ * ? : \  空格
     * @param input
     * @return
     */
    public static String transformMetachar(String regex,String input){
         StringBuffer sb = new StringBuffer();
        try{
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            while(matcher.find()){
                String g= matcher.group()+"\"";
                matcher.appendReplacement(sb, g);
            }
            matcher.appendTail(sb);
        } catch(Exception e){
            LOG.error("solr transformMetachar error:"+e);
        }
        return sb.toString();
    }
    
    /**
     * 
    * @Title: replaceflag 如果判断到有^号的时候，那么对查询值加双引号
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public static String replaceFlag(String str){
        String[] array=str.split("\\:");
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<array.length;i++){
            String t=array[i];
            if(t.contains("^")){
                String regex = "\\^([0-9]+\\.[0-9]+)";
                String tmp=transformMetachar(regex,t);
                if(!t.equals(tmp)){
                    t=tmp;
                }else{
                    regex = "\\^([0-9]+)";
                    t=transformMetachar(regex,t);
                }
                t="\""+t;
            }
            if(i!=array.length-1){
                sb.append(t).append(":");
            }else{
                sb.append(t);
            }
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        String str="multi_tagsort:一线  AND -multi_tagsort:二线^1.1";
        System.out.println(replaceFlag(str));
    }

    public static SolrDocumentList queryDocs(HttpSolrServer client, Map<String, Object> map) throws IOException {

        String query = "";
        if (map.get("q") != null) {
            query = map.get("q").toString();
        } else {
            query = "*:*";
        }
        if(query.contains("^")){
            query=replaceFlag(query);
        }
        
        int start = map.get("start") == null ? 0 : Integer.valueOf(map.get("start").toString());
        int rows = map.get("rows") == null ? 0 : Integer.valueOf(map.get("rows").toString());

        SolrQuery params = new SolrQuery(query);
        params.setStart(start);
        params.setRows(rows);
        
        Object fq = map.get("fq");
        if (fq != null) {
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(fq);
            addFilterQuery(params, jsonObject);
        }
        Object sort = map.get("sort");
        if (sort != null) {
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(sort);
            addSortQuery(params, jsonObject);
        }

        Object fl = map.get("fl");
        String flStr="";
        if (fl != null) {
            flStr=fl.toString();
        }else{
            flStr="*";
        }

        if(map.get("groupBy")!=null){
            String groupBy=map.get("groupBy").toString();
            if(StringUtils.isNotEmpty(groupBy)){
                Map<String,Object> groupParam=DataUtil.parse(groupBy, Map.class);
                String groupExp=groupParam.get("groupExp").toString();
                String groupSortExp=groupParam.get("groupSortExp").toString();
                flStr=flStr+","+groupExp+","+groupSortExp;
                if(groupParam.get("innerGroupSortExp")!=null){
                    flStr=flStr+","+groupParam.get("innerGroupSortExp").toString();
                }
                params.setStart(0);
                params.setRows(999999999);//如果是分组需求那么闲不设置rows，待分完组后再取rows行数据
                //params.setRows(20000);//如果是分组需求那么闲不设置rows，待分完组后再取rows行数据
            }
        }
        
        params.set("fl", flStr);
        
        Object bf = map.get("bf");
        if (bf != null) {
            params.set("defType", "edismax");
            params.set("bf", bf.toString());
        }

        SolrDocumentList list = null;
        try {
            QueryResponse response = client.query(params);
            list = response.getResults();
        } catch (Exception e) {
            LOG.error("solr query error:"+e);
            return new SolrDocumentList();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static void addSortQuery(SolrQuery params, Object objJson) {
        if (objJson instanceof net.sf.json.JSONArray) {
            net.sf.json.JSONArray objArray = (net.sf.json.JSONArray) objJson;
            for (int i = 0; i < objArray.size(); i++) {
                addSortQuery(params, objArray.get(i));
            }
        } else if (objJson instanceof net.sf.json.JSONObject) {
            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) objJson;
            Iterator<Object> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                if (object instanceof net.sf.json.JSONArray) {
                    net.sf.json.JSONArray objArray = (net.sf.json.JSONArray) object;
                    addSortQuery(params, objArray);
                } else if (object instanceof net.sf.json.JSONObject) {
                    addSortQuery(params, (net.sf.json.JSONObject) object);
                } else {
                    if ("asc".equalsIgnoreCase(object.toString())) {
                        params.addSort(key, SolrQuery.ORDER.asc);
                    } else {
                        params.addSort(key, SolrQuery.ORDER.desc);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addFilterQuery(SolrQuery params, Object objJson) {
        if (objJson instanceof net.sf.json.JSONArray) {
            net.sf.json.JSONArray objArray = (net.sf.json.JSONArray) objJson;
            for (int i = 0; i < objArray.size(); i++) {
                addFilterQuery(params, objArray.get(i));
            }
        } else if (objJson instanceof net.sf.json.JSONObject) {
            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) objJson;
            Iterator<Object> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                if (object instanceof net.sf.json.JSONArray) {
                    net.sf.json.JSONArray objArray = (net.sf.json.JSONArray) object;
                    addFilterQuery(params, objArray);
                } else if (object instanceof net.sf.json.JSONObject) {
                    addFilterQuery(params, (net.sf.json.JSONObject) object);
                } else {
                    String[] array = object.toString().split(",");
                    for (String str : array) {
                        if (!isRangeExp(str)) {
                            str = "\"" + str + "\"";
                        }
                        params.addFilterQuery(key + ":" + str);
                    }
                }
            }
        }
    }

    public static boolean isRangeExp(String value) {
        Matcher m = rangePattern.matcher(value);
        return m.find();
    }

    public static SolrInputDocument createDoc(HttpSolrServer client, Map<String, String> map) {
        SolrInputDocument doc = new SolrInputDocument();
        for (String key : map.keySet()) {
            doc.addField(key, map.get(key));
        }
        return doc;
    }

    public static void addDoc(HttpSolrServer client, SolrInputDocument doc) {
        try {
            client.add(doc);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    
    public static Map<String,String> updateDoc(String collectionName,String id,String refreshFieldName,String refreshFieldValue) {
        
            Map<String,String> result=new HashMap<String, String>();
            StringBuffer errors=new StringBuffer();
            String url = SysConfig.getValue("solr.solrServerUrl") + "/" + collectionName;
            HttpSolrServer client = SolrClientUtil.getInstance(url);
            SolrDocumentList docs;
            if(StringUtils.isNotEmpty(collectionName)){
                result.put("result", "fail");
                result.put("error", "collectionName不能为空");
                return result;
            }
            if(StringUtils.isNotEmpty(id)){
                result.put("result", "fail");
                result.put("error", "id不能为空");
                return result;
            }
            if(StringUtils.isNotEmpty(refreshFieldName)){
                result.put("result", "fail");
                result.put("error", "refreshFieldName不能为空");
                return result;
            }
            if(StringUtils.isNotEmpty(refreshFieldValue)){
                result.put("result", "fail");
                result.put("error", "refreshFieldValue不能为空");
                return result;
            }
            
            try {
                docs = SolrClientUtil.queryDocs(client, "id:"+id);
                if (ListUtil.notEmpty(docs)){
                    SolrDocument doc=docs.get(0);
                    Collection<String> fieldNames=doc.getFieldNames();
                    SolrInputDocument sidoc=new SolrInputDocument();
                    for(String fieldName:fieldNames){
                        sidoc.addField(fieldName, doc.getFieldValue(fieldName));
                    }
                    sidoc.remove("_version_");
                    sidoc.setField(refreshFieldName, refreshFieldValue);
                    SolrClientUtil.addDoc(client, sidoc);
                    result.put("result", "success");
                }else{
                    errors.append("updateDoc error").append(",collectionName:").append(collectionName).append(",id").append(id).append("的数据没有找到").append(",refreshFieldName").append(refreshFieldName).append(",refreshFieldValue").append(refreshFieldValue);
                    LOG.error(errors.toString());
                    result.put("result", "fail");
                    result.put("error", errors.toString());
                }
            } catch (IOException e) {
               errors.append("updateDoc error:").append(e).append(",collectionName:").append(collectionName).append(",id:").append(id).append(",refreshFieldName").append(refreshFieldName).append(",refreshFieldValue").append(refreshFieldValue);
               LOG.error(errors.toString());
               result.put("result", "fail");
               result.put("error", errors.toString());
            }
            return result;
    }

    public static void addDocs(HttpSolrServer client, List<SolrInputDocument> docs) {
        try {
            client.add(docs);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void delDoc(HttpSolrServer client, String id) {
        try {
            client.deleteById(id);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delDocs(HttpSolrServer client, List<String> ids) {
        try {
            client.deleteById(ids);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void requestConvertForSumInfo(String collectionName,String source,Map<String,String> resultMap){
        String func="sumInfo(";
        if(!source.contains(func)){
            resultMap.put("convertResult", source);
            return;
        }
        int funcLength=func.length();
        String newFunName="sumAdvance";
        int index1=source.indexOf(func);
        String tmp=source.substring(index1+funcLength);
        String tmp1=tmp.substring(0,tmp.indexOf(")"));
        int index2=index1+funcLength+tmp1.length()+1;
        String[] arrayTmp=tmp1.split(",");
        String fieldName=arrayTmp[0];
        String word=arrayTmp[1];
        String flag=arrayTmp[2];
        StringBuffer replaceString=new StringBuffer();
        String replaceString1="";
        if(!"1".equals(flag)){
            //不做分词处理
            replaceString.append(newFunName).append("(");
            replaceString.append("weight").append("_").append(fieldName).append("_").append(word).append(")");
            replaceString1=replaceString.toString();
        }else{
            String tmpString=resultMap.get(fieldName+"_"+word);
            if(StringUtils.isNotEmpty(tmpString)){
                replaceString1=tmpString; 
            }else{
                try {
                    String[] array=fencis.get(word);
                    if(array==null || array.length==0){
                        long begin=System.currentTimeMillis();
                        String solrServerUrl = "http://solr.lejj.com:8181/solr";
                        String schemaName=collectionName;
                        String fieldtype="string";
                        StringBuffer urlStringBuffer = new StringBuffer();
                        urlStringBuffer.append(solrServerUrl).append("/").append(schemaName).append("/analysis/field")
                        .append("?wt=").append(wt).append("&analysis.showmatch=true").append("&analysis.fieldvalue=")
                        .append(URLEncoder.encode(word, URL_ENCODE)).append("&analysis.query=")
                        .append(URLEncoder.encode(word, URL_ENCODE)).append("&analysis.fieldtype=").append(fieldtype);
                        String result = com.meiqi.app.common.utils.HttpUtil.getHtmlContent(urlStringBuffer.toString());
                        result = SolrUtil.formatJson(result, fieldtype);
                        Map<String,Object> map=DataUtil.parse(result,Map.class);
                        String indexResult=map.get("index").toString();
                        LOG.info("requestConvertForSumInfo get fenci indexResult1:"+indexResult);
                        indexResult=indexResult.substring(1, indexResult.length()-1);
                        LOG.info("requestConvertForSumInfo get fenci indexResult2:"+indexResult);
                        array=indexResult.split(",");
//                        if(array!=null && array.length!=0){
//                            synchronized (fencis) {
//                                fencis.put(word, array);
//                            }
//                        }
                        replaceString.append(newFunName).append("(");
                        replaceString.append("weight").append("_").append(fieldName).append("_").append(word).append(",");
                        long time=System.currentTimeMillis()-begin;
                        LOG.info("requestConvertForSumInfo get fenci time:"+time+",word:"+word);
                    }else{
                        LOG.info("requestConvertForSumInfo get cache word:"+word);
                    }
                    for(String str:array){
                        String value=str.substring(1, str.length()-1);
                        if(!word.equals(value)){
                            replaceString.append("weight").append("_").append(fieldName).append("_").append(value).append(",");
                        }
                    }
                    replaceString=new StringBuffer(replaceString.substring(0, replaceString.lastIndexOf(",")));
                    replaceString.append(")");
                    replaceString1=replaceString.toString();
                    resultMap.put(fieldName+"_"+word, replaceString1);
                } catch (Exception e) {
                    LOG.info("requestConvertForSumInfo error:"+e);
                }
            }
        }
        String lastStr=source.substring(0, index1)+replaceString1+source.substring(index2);
        resultMap.put("convertResult", lastStr);
        if(lastStr.contains(func)){
            requestConvertForSumInfo(collectionName,lastStr,resultMap);
        }
    }
    
    /**
     * 调用SolrServer增量刷新solr数据
    * @Title: refreshSolr 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param index_name
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public static String refreshSolr(String index_name){
        String SolrServerProjectUrl=SysConfig.getValue("solr.SolrServerProjectUrl");
        String url=SolrServerProjectUrl+"/solr/refreshSolrData.do?{\"index_name\":\""+index_name+"\"}";
        String result="";
        try {
            result = HttpExecutor.get(url);
        } catch (Exception e) {
            String error="refreshSolr error:"+e;
            LOG.info(error);
            return error;
        }
        return result;
    }
    
    /**
     * 
    * @Title: queryByGroup 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param server
    * @param @param qStr
    * @param @param facetFields
    * @param @param asc 是否按count的从大到小排序
    * @param @param start
    * @param @param rows
    * @param @return  参数说明 
    * @return Map<String,Object>    返回类型 
    * @throws
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> queryByGroup(HttpSolrServer server,Map<String, Object> params){
        
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> resultRows = new ArrayList<Map<String, Object>>();
        try {
            String query = "";
            if (params.get("q") != null) {
                query = params.get("q").toString();
            } else {
                query = "*:*";
            }
            if(query.contains("^")){
                query=replaceFlag(query);
            }
            String start = params.get("start") == null ? "0" :params.get("start").toString();
            String rows = params.get("rows") == null ? "0" : params.get("rows").toString();
            
            SolrQuery param = new SolrQuery(query);  
            
            Object fq = params.get("fq");
            if (fq != null) {
                net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(fq);
                addFilterQuery(param, jsonObject);
            }
            
            param.setStart(Integer.parseInt(start));
            param.setParam(GroupParams.GROUP, true);
            String facetFields=params.get("facetFields")==null?"":params.get("facetFields").toString();
            String[] facetFieldsArray=facetFields.split(",");
            param.setParam(GroupParams.GROUP_FIELD, facetFieldsArray);  
            param.setParam(GroupParams.GROUP_LIMIT, "1");
            param.setParam("group.ngroups", true);//是否计算所得分组个数；注意：当每个分组显示数目大于1个时，不能用分组数量来计算总页码
            Object facetFieldSort=params.get("facetFieldSort");
            if(facetFieldSort!=null){
                Map<String,String> m=DataUtil.parse(facetFieldSort.toString(),Map.class);
                String[] array=new String[m.size()];
                int i=0;
                for(String key:m.keySet()){
                    array[i]=key+" "+m.get(key);
                    i++;
                }
                param.setParam("sort", array);
            }
            param.setRows(Integer.parseInt(rows));
            QueryResponse response = null;  
            response = server.query(param); 
            GroupResponse groupResponse = response.getGroupResponse();
            if(groupResponse != null) {  
                List<GroupCommand> groupList = groupResponse.getValues();
                for(GroupCommand groupCommand : groupList) {
                    List<Group> groups = groupCommand.getValues();
                    map.put("total", groupCommand.getNGroups().longValue());
                    for(Group group : groups) {  
                        Map<String, Object> m=new HashMap<String, Object>();
                        String name=groupCommand.getName();
                        String value=group.getGroupValue();
                        if(StringUtils.isNotEmpty(value)){
                            m.put(name, value);
                            m.put("total",(int)group.getResult().getNumFound());
                            resultRows.add(m);
                        }
                    }  
                }  
            }  
        } catch (Exception e) {
           LOG.info("queryByGroup error:"+e);
        }
        map.put("rows", resultRows);
        return map;
       } 
    
    
    /**
     * 
    * @Title: 分组后获取没组的其中一条数据
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param server
    * @param @param qStr
    * @param @param facetFields
    * @param @param asc 是否按count的从大到小排序
    * @param @param start
    * @param @param rows
    * @param @return  参数说明 
    * @return Map<String,Object>    返回类型 
    * @throws
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> querySolrSimpleDataByGroup(HttpSolrServer server,Map<String, Object> params){
        
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> resultRows = new ArrayList<Map<String, Object>>();
        try {
            String query = "";
            if (params.get("q") != null) {
                query = params.get("q").toString();
            } else {
                query = "*:*";
            }
            if(query.contains("^")){
                query=replaceFlag(query);
            }
            String start = params.get("start") == null ? "0" :params.get("start").toString();
            String rows = params.get("rows") == null ? "0" : params.get("rows").toString();
            
            SolrQuery param = new SolrQuery(query);  
            
            Object fq = params.get("fq");
            if (fq != null) {
                net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(fq);
                addFilterQuery(param, jsonObject);
            }
            
            Object fl = params.get("fl");
            String flStr="";
            if (fl != null) {
                flStr=fl.toString();
            }else{
                flStr="*";
            }
            param.set("fl", flStr);
            
            param.setStart(Integer.parseInt(start));
            param.setParam(GroupParams.GROUP, true);
            String facetFields=params.get("facetFieldsForData")==null?"":params.get("facetFieldsForData").toString();
            String[] facetFieldsArray=facetFields.split(",");
            param.setParam(GroupParams.GROUP_FIELD, facetFieldsArray);  
            param.setParam(GroupParams.GROUP_LIMIT, "1");
            param.setParam("group.ngroups", true);//是否计算所得分组个数；注意：当每个分组显示数目大于1个时，不能用分组数量来计算总页码
            Object facetFieldSort=params.get("facetFieldSort");
            if(facetFieldSort!=null){
                Map<String,String> m=DataUtil.parse(facetFieldSort.toString(),Map.class);
                String[] array=new String[m.size()];
                int i=0;
                for(String key:m.keySet()){
                    array[i]=key+" "+m.get(key);
                    i++;
                }
                param.setParam("sort", array);
            }
            param.setRows(Integer.parseInt(rows));
            QueryResponse response = null;  
            response = server.query(param); 
            GroupResponse groupResponse = response.getGroupResponse();
            if(groupResponse != null) {
                List<GroupCommand> groupList = groupResponse.getValues();
                for(GroupCommand groupCommand : groupList) {
                    List<Group> groups = groupCommand.getValues();
                    map.put("total", groupCommand.getNGroups().longValue());
                    for(Group group : groups) {  
                        SolrDocumentList list=group.getResult();
                        if(ListUtil.notEmpty(list)){
                            SolrDocument doc=list.get(0);
                            Map<String, Object> map1 = new HashMap<String, Object>();
                            Set<String> keys = doc.keySet();
                            for (String key : keys) {
                                map1.put(key, doc.get(key));
                            }
                            resultRows.add(map1);
                        }
                    }  
                }
            }  
        } catch (Exception e) {
           LOG.info("querySolrSimpleDataByGroup error:"+e);
        }
        map.put("rows", resultRows);
        return map;
       } 
}

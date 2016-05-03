package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.ListUtil;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.sort.AttrInfo;
import com.meiqi.openservice.bean.sort.ReqUrlInfo;
import com.meiqi.openservice.bean.sort.RespUrlInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.util.MyApplicationContextUtil;

@Service
public class SortLejjAction extends BaseAction{
	public static final Logger LOG = Logger.getLogger(SortLejjAction.class);
	private static Map<String, String> sortIdType = new HashMap<String, String>(64);
	private static Map<String, String> sortIdName = new HashMap<String, String>(64);
	private static List<Map<String, String>> sortlist = new ArrayList<Map<String, String>>();
	@Autowired
	private IDataAction dataAction;
	@Autowired
	private IPushAction pushAction;
	@Autowired
	private SolrAction solrAction;
	@Autowired
    private ISolrAction iSolrAction;
	/**
	 * 标签推荐
	 * @return
	 */
	public String tagRecommend(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo){
		long startTime=System.currentTimeMillis();
		if (0 == sortIdName.size()) {
			queryNewFromDb();
		}
		// 提取请求参数参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(),Map.class);
		// 得到用户请求的url参数 /albums/88-c-29/
		String inputUrl = (String) param.get("input_url").toString();
		
		//当前选中的的标签id
		List<Integer> tagList=new ArrayList<Integer>();
		//solr keywords
		StringBuilder keyBuilder=new StringBuilder();
		//如果url包含search/albums- 那么url格式为 http://www.aiuw.com/search/albums-69050.html
		if(inputUrl.contains("search/albums-")){
			
			
			String[] tempArray=inputUrl.split("/");
			if(1<tempArray.length){
				String tempSS=tempArray[tempArray.length-1];
				tempSS=queryKeyWordsFromSolr("keywords_url:"+tempSS);
				keyBuilder.append(tempSS);
			}
		}else if(inputUrl.contains("search/albums/?keywords=")){ //如果url包含keywords则为http://www.aiuw.com/search/albums/?keywords=10平米书房1
			String[] tempArray=inputUrl.split("=");
			if(2==tempArray.length){
				keyBuilder.append(tempArray[1]);
			}
		}else{
			String newAlbumsString=newAlbums(request, response, repInfo);
			JSONObject newAlbumsJson=JSONObject.parseObject(newAlbumsString);
			JSONArray newAlbumsJsonArray=newAlbumsJson.getJSONArray("rows");
			int newAlbumsJsonArraySize=newAlbumsJsonArray.size();
			for(int i=0;i<newAlbumsJsonArraySize;i++){
				JSONObject jsonO=newAlbumsJsonArray.getJSONObject(i);
				tagList.add(jsonO.getInteger("id"));
				keyBuilder.append(jsonO.get("name"));
				if((1+i)<newAlbumsJsonArraySize){
					keyBuilder.append(",");
				}
			}
		}
		
		JSONObject paramJson=new JSONObject();
		paramJson.put("row_num", "20");
		paramJson.put("st_num", "0");
		paramJson.put("keywords", keyBuilder.toString());
		paramJson.put("url_type", "p_search_tag");
		paramJson.put("input_url", inputUrl);
		repInfo.setParam(paramJson.toJSONString());
		String solrString=solrAction.queryByConvert(request, response, repInfo);
		JSONObject solrJson=JSONObject.parseObject(solrString);
		JSONArray solrJsonArray=solrJson.getJSONArray("rows");
		int solrJsonArraySize=solrJsonArray.size();
		//新建一个临时派讯用list
		List<Integer> tempList=new ArrayList<Integer>();
		for(int i=0;i<solrJsonArraySize;i++){
			JSONObject solrO=solrJsonArray.getJSONObject(i);
			String key_type=solrO.getString("key_type");
			
			if("tag".equals(key_type)){
				String tag_url=solrO.getString("tag_url");
				Integer tagUrl=Integer.parseInt(tag_url);
				//开始拼接id
				StringBuilder tempUrl=new StringBuilder("/albums/c-");
				//进行拷贝
				//先进行addall使tempList元素数量足够多 防止tempList size小于tagList情况直接使用copy会发生IndexOutOfBoundsException异常
				Collections.addAll(tempList,  new Integer[tagList.size()]); 
				Collections.copy(tempList, tagList);
				tempList.add(tagUrl);
				//进行自然排序
				Collections.sort(tempList);
				int tempListSize=tempList.size();
				for(int q=0;q<tempListSize;q++){
					tempUrl.append(tempList.get(q));
					if((q+1)==tempListSize){
						tempUrl.append("/");
					}else{
						tempUrl.append("-");
					}
				}
				//使用完清除
				tempList.clear();
				solrO.put("tag_url", tempUrl);
			}
		}
		long endTime=System.currentTimeMillis();
		LOG.info("sortLejj tagRecommend time(ms):" + (endTime - startTime));
		return solrJson.toJSONString();
	}
	
	
	/*
	 * 乐家居 分类标签 新接口
	 */
	public String newAlbums(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		// 检查缓存中是否有数据没有则从数据库获取
		long startTime=System.currentTimeMillis();
		if (0 == sortIdName.size()) {
			queryNewFromDb();
		}
		// 提取请求参数参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(),Map.class);
		// 得到用户请求的url参数 /albums/88-c-29/
		String inputUrl = (String) param.get("input_url").toString().replaceAll("/","");
		//用-切割字符串
		String[] inputSlashArray=inputUrl.split("-");
		int inputSlashArrayLength=inputSlashArray.length;
		String tempKeyString=null,tempValueString=null;
		List<Map<String,String>> newSortInfoList=new ArrayList<Map<String,String>>();
		for(int i=0;i<inputSlashArrayLength;i++){
			tempKeyString=inputSlashArray[i];
			//如果为数字
			if(true==StringUtils.isNumeric(tempKeyString)){
				tempValueString=sortIdName.get(tempKeyString);
				if(null!=tempValueString){
					Map<String,String> newSortInfoMap=new HashMap<String, String>();
					newSortInfoMap.put("id",tempKeyString);
					newSortInfoMap.put("name", tempValueString);
					newSortInfoList.add(newSortInfoMap);
				}
			}

		}
		// 封装成规则引擎数据格式返回
		RuleServiceResponseData responseData = new RuleServiceResponseData();
		responseData.setCode(DsResponseCodeData.SUCCESS.code);
		responseData.setDescription(DsResponseCodeData.SUCCESS.description);
		responseData.setRows(newSortInfoList);
		String result = JSON.toJSONString(responseData);
		long endTime=System.currentTimeMillis();
		LOG.info("sortLejj newAlbums time(ms):" + (endTime - startTime));
		return result;
	}

	public String albums(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		long start = System.currentTimeMillis();
		// 检查缓存中是否有数据没有则从数据库获取
		if (0 == sortlist.size()) {
			queryFromDb();
		}
		// 提取请求参数参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(),Map.class);
		// 得到用户请求的url参数 /albums/88-c-29/
		String inputUrl = (String) param.get("input_url");
		// 以/截取字符串，得到详细参数
		String[] inputSlashArray = inputUrl.split("/");
		// 声明一个对象存储请求的url信息
		ReqUrlInfo urlInfo = new ReqUrlInfo();
		// 声明一个临时字符串变量存储截取出的参数
		String tempParam = "";
		
			if (3 == inputSlashArray.length) {
				// 如果包含c则表明是参数字符串，赋值给临时变量
//				if (-1 < (inputSlashArray[2].indexOf("c-"))) {
//					tempParam = inputSlashArray[2];
//				}
				tempParam = inputSlashArray[2];
			}

		if (!"".equals(tempParam)) {
			if(tempParam.contains("newest-page")){
				readParam(urlInfo, new String[]{"newest"}, 0);
			}else{
				// 将参数以-截取
				String[] paramArray = tempParam.split("-");
				if(1<paramArray.length){
					readParam(urlInfo, paramArray, 1);
				}else{
					readParam(urlInfo, paramArray,0);
				}
			}
		}

		List<RespUrlInfo> styletagList = new ArrayList<RespUrlInfo>();
		List<RespUrlInfo> areatagList = new ArrayList<RespUrlInfo>();
		List<RespUrlInfo> detailtagList = new ArrayList<RespUrlInfo>();
		List<RespUrlInfo> squaretagList = new ArrayList<RespUrlInfo>();
		for (Map<String, String> tempMap : sortlist) {
			RespUrlInfo respUrlInfo = new RespUrlInfo();
			respUrlInfo.setName(tempMap.get("title"));
			String parent_title = tempMap.get("parent_title");
			String num = tempMap.get("id");
			//判断类型是否为风格、空间、局部、户型
			if("风格空间局部户型".contains(parent_title)){
				respUrlInfo.setUrl(generateUrl(urlInfo, num, parent_title));

				if (urlInfo.getStyletagNum().equals("-" + num)
						|| urlInfo.getAreatagNum().equals("-" + num)
						|| urlInfo.getDetailtagNum().equals("-" + num)
						|| urlInfo.getSquaretagNum().equals("-" + num)
						) {
					respUrlInfo.setIs_select("1");
				} else {
					respUrlInfo.setIs_select("0");
				}
				if ("风格".equals(parent_title)) {
					styletagList.add(respUrlInfo);
				} else if ("空间".equals(parent_title)) {
					areatagList.add(respUrlInfo);
				} else if ("局部".equals(parent_title)) {
					detailtagList.add(respUrlInfo);
				} else if ("户型".equals(parent_title)) {
					squaretagList.add(respUrlInfo);
				} 

			}
		}

		AttrInfo styletags = new AttrInfo("风格", styletagList);
		AttrInfo areatags = new AttrInfo("空间", areatagList);
		AttrInfo detailtags = new AttrInfo("局部", detailtagList);
		AttrInfo squaretags = new AttrInfo("户型", squaretagList);

		// 今日最热
		List<RespUrlInfo> hottagList = new ArrayList<RespUrlInfo>();
		String tempS = "0";
		if ("".equals(urlInfo.getExtra())) {
			tempS = "1";
		}
		RespUrlInfo utToday = new RespUrlInfo("今日最热", urlInfo.getHotUrl(inputUrl),
				tempS);
		hottagList.add(utToday);
		AttrInfo hottags = new AttrInfo("今日最热", hottagList);

		// 最新
		List<RespUrlInfo> newtagList = new ArrayList<RespUrlInfo>();
		tempS = "0";
		if ("-newest".equals(urlInfo.getExtra())) {
			tempS = "1";
		}
		RespUrlInfo utNew = new RespUrlInfo("最新发布", urlInfo.getNewUrl(inputUrl), tempS);
		newtagList.add(utNew);
		AttrInfo newtags = new AttrInfo("最新发布", newtagList);

//		// 推荐
//		List<RespUrlInfo> recommendtagList = new ArrayList<RespUrlInfo>();
//		tempS = "0";
//		if ("-recommend".equals(urlInfo.getExtra())) {
//			tempS = "1";
//		}
//		RespUrlInfo utRecommend = new RespUrlInfo("设计师推荐",
//				urlInfo.getRecommendUrl(), tempS);
//		recommendtagList.add(utRecommend);
//		AttrInfo recommendtags = new AttrInfo("设计师推荐", recommendtagList);

		
		

		// 城市
//		List<RespUrlInfo> citytagList = new ArrayList<RespUrlInfo>();
//		tempS = "0";
//		if (1 > urlInfo.getCity().length()) {
//			tempS = "1";
//		}
//		RespUrlInfo utCityQg = new RespUrlInfo("全国", urlInfo.getCityUrl(""),
//				tempS);
//		tempS = "0";
//		if ("-city234".equals(urlInfo.getCity())) {
//			tempS = "1";
//		}
//		RespUrlInfo utCitySz = new RespUrlInfo("深圳",
//				urlInfo.getCityUrl("-city234"), tempS);
//		tempS = "0";
//		if ("-city272".equals(urlInfo.getCity())) {
//			tempS = "1";
//		}
//		RespUrlInfo utCityCd = new RespUrlInfo("成都",
//				urlInfo.getCityUrl("-city272"), tempS);
//		citytagList.add(utCityQg);
//		citytagList.add(utCitySz);
//		citytagList.add(utCityCd);
//		AttrInfo citytags = new AttrInfo("城市", citytagList);

		List<AttrInfo> sortSet = new ArrayList<AttrInfo>();
		sortSet.add(hottags);
//		sortSet.add(citytags);
		sortSet.add(newtags);
//		sortSet.add(recommendtags);
		sortSet.add(styletags);
		sortSet.add(areatags);
		sortSet.add(detailtags);
		sortSet.add(squaretags);
		// 封装成规则引擎数据格式返回
		RuleServiceResponseData responseData = new RuleServiceResponseData();
		responseData.setCode(DsResponseCodeData.SUCCESS.code);
		responseData.setDescription(DsResponseCodeData.SUCCESS.description);
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("url_str", JSON.toJSONString(sortSet));
		resultList.add(resultMap);
		responseData.setRows(resultList);
		long end = System.currentTimeMillis();
		LOG.info("sortLejj albums time(ms):" + (end - start));
		String result = JSON.toJSONString(responseData);
//		String keyStr = "{\"action\":\"sortLejjAction\",\"method\":\"albums\",\"param\":{\"input_url\":\""
//				+ inputUrl + "\",\"return\":\"solr_style\"},\"needAll\":1}";
//		String key = MD5Util.MD5(keyStr);
		
		return result;
	}

	// 生成url
	private String generateUrl(ReqUrlInfo reqUrlInfo, String num,
			String tempTypeString) {
		return reqUrlInfo.getUrlCount0(tempTypeString, num);
		
	}

	
	public String reloadTag(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo) {
		sortIdType.clear();
		sortIdName.clear();
		sortlist.clear();
		try {
			pushAction.updateService("ZXPIC_BUV1_toptag2");
			pushAction.updateService("ZXPIC_HUV1_pictaginfo");
			pushAction.updateService("ZXPIC_BUV1_pictagid");
			pushAction.updateService("LJJ_BUV1_tag_info");
			pushAction.updateService("ZXPIC_HUV2_pictaginfo");
			queryNewFromDb();
			queryFromDb();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "重载成功！";
	}
	
	/**
	 * 读取参数
	 * 
	 * @param urlInfo
	 * @param param
	 * @param begin
	 */
	private void readParam(ReqUrlInfo urlInfo, String[] param, int begin) {
		for (int i = begin; i < param.length; i++) {
			// 循环将参数进行解析设置
			setReqNum(urlInfo, param[i]);
		}
	}

	// 设置提交的url的参数
	private void setReqNum(ReqUrlInfo urlInfo, String tempNum) {
		if ("newest".equals(tempNum)) {// 若果是最新
			urlInfo.setExtra("-newest");
			urlInfo.setNo_extra("newest");
		} else if ("recommend".equals(tempNum)) {// 如是推荐
			urlInfo.setExtra("-recommend");
			urlInfo.setNo_extra("recommend");
		} else if ("hot".equals(tempNum)) { // 最热
			urlInfo.setExtra("-hot");
			urlInfo.setNo_extra("hot");
		} else if ("city234".equals(tempNum)) { // 城市为深圳
			urlInfo.setCity("-city234");
		} else if ("city272".equals(tempNum)) { // 城市为成都
			urlInfo.setCity("-city272");
		}else if(null!=tempNum||!"".equals(tempNum)){
			if(StringUtils.isNumeric(tempNum)){
				urlInfo.addCount(1);
			}
		}
	}

	/*
	 * 调用规则引擎查询数据库
	 */
	public static void queryNewFromDb() {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("LJJ_BUV1_tag_info");
		dsManageReqInfo.setNeedAll("1");
		// dsManageReqInfo.setParam(param);
		IDataAction dataAction=(IDataAction)MyApplicationContextUtil.getBean("dataAction");
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil
				.parse(resultData, RuleServiceResponseData.class);
		if(ListUtil.notEmpty(responseData.getRows())){
            List<Map<String, String>> newsortSet = responseData.getRows();
            for (Map<String, String> urlMap : newsortSet) {
                sortIdName.put(urlMap.get("id"), urlMap.get("title"));
            }
        }
	}
	
	
	/*
	 * 调用规则引擎查询数据库
	 */
	public static void queryFromDb() {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("ZXXG_HSV1_TagsAdPosition");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("ad_code", "lejjalbums_1506_tags");
		dsManageReqInfo.setParam(param);
		IDataAction dataAction=(IDataAction)MyApplicationContextUtil.getBean("dataAction");
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil
				.parse(resultData, RuleServiceResponseData.class);
		if(ListUtil.notEmpty(responseData.getRows())){
		    List<Map<String,String>> mapList=responseData.getRows();
	        String parent_title="风格";
	        for(int q=0;q<mapList.size();q++){
	            Map<String,String> map=mapList.get(q);
	            parent_title=map.get("tag_name1");
	            JSONArray jsonArray=JSONArray.parseArray(map.get("tag_name"));
	            int jsonArraySize=jsonArray.size();
	            JSONObject jo=null;
	            for(int i=0;i<jsonArraySize;i++){
	                jo=jsonArray.getJSONObject(i);
	                if(null==sortIdType.get(jo.getString("tag_id"))){
	                    Map<String,String> joMap=new HashMap<String, String>();
	                    joMap.put("title", jo.getString("tag_name"));
	                    joMap.put("id", jo.getString("tag_id"));
	                    joMap.put("parent_title", parent_title);
	                    sortIdType.put(jo.getString("tag_id"), parent_title);
	                    sortlist.add(joMap);
	                }
	            }
	        }
		}
	}

	
	private String queryKeyWordsFromSolr(String where){
		DsManageReqInfo dsReqInfo = new DsManageReqInfo();
		dsReqInfo.setServiceName("solr_search_keywords_url");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("q", where);
		param.put("start", "0");
		param.put("rows", "1");
		dsReqInfo.setParam(param);
		String resultData = iSolrAction.query(dsReqInfo);
		JSONObject jsonResultData=JSONObject.parseObject(resultData);
		JSONArray jsonArray=jsonResultData.getJSONArray("rows");
		if(null==jsonArray||0==jsonArray.size()){
			return "";
		}
		JSONObject j=jsonArray.getJSONObject(0);
		return j.getString("keywords");
	}
}

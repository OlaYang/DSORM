package com.meiqi.openservice.commons.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.Cache4D2Data;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.Services;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.impl.DataActionImpl;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.util.MyApplicationContextUtil;

public class Tool{
	private static final Logger LOG = Logger.getLogger(Tool.class);
	
	/**
	 * 
	* @Title: verifyCode 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param request
	* @param @param code
	* @param @param codeType
	* @param @param setToInvalid 是否设置为失效 true为要失效
	* @param @return  参数说明 
	* @return boolean    返回类型 
	* @throws
	 */
    public static boolean verifyCode(HttpServletRequest request, String code, String codeType,boolean setToInvalid )
    {
        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(codeType)){
            return false;
        }
        String verifyCode = (String)request.getSession().getAttribute(Constants.NormalVerifyCodeType.get(codeType));
        LOG.info("codeType:"+codeType+",verifyCode:"+verifyCode+",seeeionId:"+request.getSession().getId());
        boolean result=code.equalsIgnoreCase(verifyCode);
        if(result && setToInvalid){
            request.getSession().removeAttribute(Constants.NormalVerifyCodeType.get(codeType));
        }
        return result;
    }
    
    /**
     * 查询规则的公共方法
     * @param ruleName  规则名
     * @param param  参数
     * @param obj  日志对象
     * @param str 
     * @param str1
     * @return
     */
    public  Object getRuleResult(String ruleName,Map<String,Object> param,Object obj,String str,String str1){
    	Logger logger = null;
    	Log log = null;
    	boolean flag = false;
    	if(obj instanceof Logger){
    		logger = (Logger)obj;
    	}else if(obj instanceof Log){
    		log = (Log)obj;
    		flag = true;
    	}
    	ResponseInfo respInfo = new ResponseInfo();
    	DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName(ruleName);
		dsManageReqInfo.setParam(param);
		DataActionImpl dataAction = (DataActionImpl) MyApplicationContextUtil.getBean("dataAction");
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		if(flag){
    		log.info(str+" "+ruleName+" param is: "+ JSONObject.toJSONString(dsManageReqInfo));
    		log.info(str+" "+ruleName+" result is: "+ resultData);
    	}else{
    		logger.info(str+" "+ruleName+" param is: "+ JSONObject.toJSONString(dsManageReqInfo));
    		logger.info(str+" "+ruleName+" result is: "+ resultData);
    	}
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			if(flag){
	    		log.error(str+" "+ruleName+" param is: "+ JSONObject.toJSONString(dsManageReqInfo));
				log.error(str+" "+ruleName+" result is: "+ resultData);
	    	}else{
	    		logger.error(str+" "+ruleName+" param is: "+ JSONObject.toJSONString(dsManageReqInfo));
	    		logger.error(str+" "+ruleName+" result is: "+ resultData);
	    	}
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询"+str1+"错误！");
			return JSON.toJSONString(respInfo);
	    }
		List<Map<String,String>> mapList = responseData.getRows();
		if(null == mapList || mapList.size() == 0){
			if(flag){
	    		log.error(str+" "+ruleName+" param is: "+ JSONObject.toJSONString(dsManageReqInfo));
				log.error(str+" "+ruleName+" result is: "+ resultData);
	    	}else{
	    		logger.error(str+" "+ruleName+" param is: "+ JSONObject.toJSONString(dsManageReqInfo));
	    		logger.error(str+" "+ruleName+" result is: "+ resultData);
	    	}
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询"+str1+"无数据！");
			return JSON.toJSONString(respInfo);
		}
		return mapList;
    	
    }
    
    /**
     * 函数查询规则的公共方法
     * @param dsReqInfo
     * @param calInfo
     * @param NAME
     * @return
     * @throws Exception
     */
    public String getData(DsManageReqInfo dsReqInfo,CalInfo calInfo,String NAME) throws Exception{
		TService servicePo = Services.getService(dsReqInfo.getServiceName());
		Map<String, Object> currentParam=dsReqInfo.getParam();
		
		
		final D2Data d2Data = Cache4D2Data.getD2Data(servicePo, currentParam, calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), NAME);
		
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

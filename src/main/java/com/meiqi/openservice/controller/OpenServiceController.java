package com.meiqi.openservice.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IAuthAction;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.liduoo.controller.AuthInfo;
import com.meiqi.liduoo.controller.HttpCallStream;
import com.meiqi.liduoo.controller.HttpStreamManager;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.jms.JmsAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.ActionMappingUtil;
import com.meiqi.openservice.commons.util.DateUtil;
import com.meiqi.openservice.commons.util.FileUtil;
import com.meiqi.openservice.commons.util.IpverifyUtil;
import com.meiqi.openservice.commons.util.VerifySqlAttackUtil;

/**
 * 
 * @ClassName: AppServiceController
 * @Description:
 * @author zhouyongxiong
 * @date 2015年6月30日 下午2:11:19
 *
 */
@Controller
@Scope("prototype")
public class OpenServiceController {
    
    private static final Log LOG =  LogFactory.getLog("slow");
    
    private static final Log EXPORT =  LogFactory.getLog("export");
    @Autowired
    private IAuthAction authAction;
    
    @Autowired
    private JmsAction   jmsAction;
    
    @Autowired
    private IDataAction dataAction;
    
    @RequestMapping(value = "/openService")
    @ResponseBody
    public Object appService(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long begin=System.currentTimeMillis();
        Cookie cookie=new Cookie("JSESSIONID",request.getSession().getId());
        cookie.setPath("/");
        response.addCookie(cookie);
        
        String methodType=request.getMethod();
        HttpMethod method = HttpMethod.valueOf(methodType);
        String content = "";
        if (HttpMethod.GET.equals(method)) {
            content = DataUtil.getNoKeyParamValue(request, content);
        } else {
            content = DataUtil.inputStream2String(request.getInputStream());
        }
        
        if (StringUtils.isBlank(content)) {
            String param = "{}";
            if (!StringUtils.isBlank(request.getParameter("param"))) {
                param = request.getParameter("param");
            }
            content = "{\"action\":\"" + request.getParameter("action") + "\",\"method\":\""
                    + request.getParameter("method") + "\",\"param\":" + param + "}";
        }
        content = content.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  
        content = content.replaceAll("\\+", "%2B");
        String decodeContent = "";
        try {
            decodeContent = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.error(e.getMessage());
        }
        if(VerifySqlAttackUtil.verify(decodeContent)){
            ResponseBaseData re=new ResponseBaseData();
            re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            re.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(re);
        }
        
        if(StringUtils.isEmpty(content)){
            ResponseBaseData re=new ResponseBaseData();
            re.setCode(DsResponseCodeData.REQINFO_NOT_RIGHT.code);
            re.setDescription(DsResponseCodeData.REQINFO_NOT_RIGHT.description);
            return JSONObject.toJSONString(re);
        }
        RepInfo repInfo = DataUtil.parse(decodeContent, RepInfo.class);
        if (null == repInfo) {
            LogUtil.error("请求参数不能为空!");
            return new ResponseInfo("1", "请求参数不能为空!");
        }
        repInfo.setMemKey(decodeContent);
        // 请求授权验证
//        boolean validateAuth = authAction.validateAuthForPc(request, Base64.encode(decodeContent.getBytes()));
//        if (!validateAuth) {
//            if (BaseAction.isFromApp(request)) {
//                BaseAction.putJsonToResponse(response, JsonUtils.getFrequentRequestErrorJson());
//                return null;
//            }
//            return JsonUtils.getFrequentRequestErrorJson();
//        }
        //检验当前ip访问的合法性
        if(!IpverifyUtil.verifyCurrentIp(request, response,content)){
            ResponseBaseData re=new ResponseBaseData();
            re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            re.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(re);
        }
        
        //特殊处理来自移动端请求
        if(BaseAction.isFromApp(request)){
            // 获取header 参数
            repInfo.setHeader(BaseAction.getHeader(request));
            
            //验证请求有效性
            /*String Authorization= request.getHeader("Authorization");
            AuthBean validateAuth = authAction.validateAuthForApp(repInfo, Authorization, "app");
            if(!validateAuth.isState()){
                LogUtil.error("验证失败：Authorization=" + Authorization + ", desc=" + validateAuth.getDescription());
                ResponseBaseData re=new ResponseBaseData();
                re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                re.setDescription(validateAuth.getDescription());
                return JSONObject.toJSONString(re);
            }*/
        }


        // 获取service实体类
        String actionName=repInfo.getAction();
        BaseAction executeAction = ActionMappingUtil.getService(actionName);
        if (null == executeAction) {
            return new ResponseInfo("1", "action[" + actionName + "]没有找到");
        }
        Method m=null;
        Object result="";
        //--Added by FrankGui, to support WeChat oauth2.0 in rule service. 2015-12-22----------------BEGIN------------
        AuthInfo authInfo = null;
		if ("true".equalsIgnoreCase(repInfo.getAuthFlag())) {
			Object cacheResult = prepareOauthInvoke(request, response, repInfo, authInfo);
			if (cacheResult != null) {
				return cacheResult;
			}
		}
		//--Added by FrankGui, to support WeChat oauth2.0 in rule service. 2015-12-22----------------END------------
	    String methodName=repInfo.getMethod();
        try {
            m = executeAction.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class,RepInfo.class);
            result = m.invoke(executeAction, request, response,repInfo);
        } catch (Exception e) {
            throw e;
        } 
        long end=System.currentTimeMillis();
        long time=end-begin;
        if(time>500){
            LOG.info("methodType="+methodType+" request openService reqInfo slow:"+content+",execute time:"+time);
        }
        jmsAction.recordIpadUserAction(request,decodeContent,JSON.toJSONString(result==null?"":result));//记录有UUID的用户行为
        //--Added by FrankGui, to support WeChat oauth2.0 in rule service. 2015-12-22----------------BEGIN------------
		if ("true".equalsIgnoreCase(repInfo.getAuthFlag())) {
			afterOauthInvoke(authInfo, result);
		}
         //--Added by FrankGui, to support WeChat oauth2.0 in rule service. 2015-12-22----------------END------------
         return result;
    }

    /**
     * 导出excel格式
     * @return
     * @throws IOException 
     * @throws Exception 
     */
    @RequestMapping(value = "/openService/exportExcel")
    @ResponseBody
    public Object exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{

        Long currentTime=System.currentTimeMillis();
        HttpSession session = request.getSession();
        Map<String,Long> downloadExcelOperate=(Map<String,Long>)session.getAttribute("downloadExcelOperate");
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setPath("/");
        response.addCookie(cookie);

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String content = "";
        if (HttpMethod.GET.equals(method)) {
            content = DataUtil.getNoKeyParamValue(request, content);
        } else {
            content = DataUtil.inputStream2String(request.getInputStream());
        }
        if (StringUtils.isBlank(content)) {
            String param = "{}";
            if (!StringUtils.isBlank(request.getParameter("param"))) {
                param = request.getParameter("param");
            }
            content = "{\"action\":\"" + request.getParameter("action") + "\",\"method\":\""
                    + request.getParameter("method") + "\",\"param\":" + param + "}";
        }
        content = content.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        String decodeContent = "";
        try {
            decodeContent = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.error(e.getMessage());
        }
        if (VerifySqlAttackUtil.verify(decodeContent)) {
            ResponseBaseData re = new ResponseBaseData();
            re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            re.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(re);
        }

        if (StringUtils.isEmpty(content)) {
            ResponseBaseData re = new ResponseBaseData();
            re.setCode(DsResponseCodeData.REQINFO_NOT_RIGHT.code);
            re.setDescription(DsResponseCodeData.REQINFO_NOT_RIGHT.description);
            return JSONObject.toJSONString(re);
        }
        RepInfo repInfo = DataUtil.parse(decodeContent, RepInfo.class);
        if (null == repInfo) {
            LogUtil.error("请求参数不能为空!");
            return new ResponseInfo("1", "请求参数不能为空!");
        }
        repInfo.setMemKey(decodeContent);

        // 检验当前ip访问的合法性
        if (!IpverifyUtil.verifyCurrentIp(request, response, content)) {
            ResponseBaseData re = new ResponseBaseData();
            re.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            re.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(re);
        }

        // 特殊处理来自移动端请求
        if (BaseAction.isFromApp(request)) {
            // 获取header 参数
            repInfo.setHeader(BaseAction.getHeader(request));
        }   

        String param = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(param, DsManageReqInfo.class);
        
        //校验重复下载
        String reqJson=JSONObject.toJSONString(dsReqInfo);
        if(downloadExcelOperate!=null){
            Long time=downloadExcelOperate.get(reqJson);
            if(time!=null){
                    //针对于同一个session的用户，需要做此判断
                    Long time1=currentTime-time;//时间差
                    long maxTime=10*1000;
                    Long time2=new Long(maxTime);//10秒
                    if(time1<time2){
                        return "操作太频繁！"+(maxTime/1000)+"秒后再试";
                    }
            }else{
                downloadExcelOperate.put(reqJson,System.currentTimeMillis());
                session.setAttribute("downloadExcelOperate",downloadExcelOperate);
            }
        }else{
            downloadExcelOperate=new HashMap<String, Long>();
            downloadExcelOperate.put(reqJson,System.currentTimeMillis());
            session.setAttribute("downloadExcelOperate",downloadExcelOperate);
        }
        
        dsReqInfo.setNeedVerifyAndRebuildData(true);//需要根据规则配置的刷选条件过滤数据
        String serviceName = dsReqInfo.getServiceName();

        Map<String, Object> params = dsReqInfo.getParam();
        String json = "";
        // 分批次查询
        Object limit_start = params.get("limit_start");
        Object limit_end = params.get("limit_end");
        int initStart = 0;
        int initEnd = 0;
        if (limit_start != null && !"".equals(limit_start.toString())) {
            initStart = Integer.parseInt(limit_start.toString());
        }
        if (limit_end != null && !"".equals(limit_end.toString())) {
            initEnd = Integer.parseInt(limit_end.toString());
        } else {
            initEnd = 200000;
        }
        int start = 0;
        int size = Integer.parseInt(SysConfig.getValue("exportDataSize") == null ? "100" : SysConfig
                .getValue("exportDataSize"));
        int flag = (initEnd - initStart) % size;
        int num = 0;
        int fileNume = 0;
        String time = DateUtil.date2String(new Date(), "yyyyMMddHHmmssms");
        String tmpDirPath = System.getProperty("user.dir") + File.separator + "tempFile";
        String fileRoot = tmpDirPath + File.separator + serviceName + "_serviceExport_" + time;
        File dir = new File(fileRoot);
        if (!dir.exists()) {
            boolean result1 = dir.mkdirs();
            EXPORT.info("create fileRoot:" + fileRoot + ",create result:" + result1);
        }
        
        if (initEnd <= size) {
                //params.put("limit_start", start);
                //params.put("limit_end", initEnd);
                dsReqInfo.setParam(params);
                EXPORT.info("=====================limit_start===================:"+dsReqInfo.getParam().get("limit_start")+",reqJson:"+JSON.toJSONString(dsReqInfo));
                json = dataAction.getData(dsReqInfo, repInfo.getMemKey().trim());
                //根据当前的规则配置，过滤导出数据
                String filePath = fileRoot + File.separator + serviceName + "_" + 1 + ".csv";
                File file = new File(filePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(json.getBytes("GBK"));
                fileOutputStream.flush();
                fileOutputStream.close();
                fileNume = 1;
        } else {
            if (flag == 0) {
                num = (initEnd - initStart) / size;
                for (int i = 1; i <= num; i++) {
                    start = ((i - 1) * size) + initStart;
                    params.put("limit_start", start);
                    params.put("limit_end", size);
                    dsReqInfo.setParam(params);
                    EXPORT.info("=====================limit_start===================:"+dsReqInfo.getParam().get("limit_start")+",reqJson:"+JSON.toJSONString(dsReqInfo));
                    json = dataAction.getData(dsReqInfo, repInfo.getMemKey().trim());
                    if(dsReqInfo.isNoDataFlag()){
                        break;
                    }
                    if (i != 1) {
                        int index = json.indexOf("\n");
                        json = json.substring(index + 1);
                    }
                    String filePath = fileRoot + File.separator + serviceName + "_" + i + ".csv";
                    File file = new File(filePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(json.getBytes("GBK"));
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileNume = i;
                }
            } else {
                int n = (initEnd - initStart) / size;
                int n1 = (initEnd - initStart) % size;
                num = n + 1;
                for (int i = 1; i <= num; i++) {
                    start = ((i - 1) * size) + initStart;
                    params.put("limit_start", start);
                    if (i == num) {
                        params.put("limit_end", n1);
                    } else {
                        params.put("limit_end", size);
                    }
                    dsReqInfo.setParam(params);
                    EXPORT.info("=====================limit_start===================:"+dsReqInfo.getParam().get("limit_start")+",reqJson:"+JSON.toJSONString(dsReqInfo));
                    json = dataAction.getData(dsReqInfo, repInfo.getMemKey().trim());
                    if(dsReqInfo.isNoDataFlag()){
                        break;
                    }
                    if (i != 1) {
                        int index = json.indexOf("\n");
                        json = json.substring(index + 1);
                    }
                    String filePath = fileRoot + File.separator + serviceName + "_" + i + ".csv";
                    File file = new File(filePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(json.getBytes("GBK"));
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileNume = i;
                }
            }
        }
        File[] files = new File[fileNume];
        for (int j = 1; j <= fileNume; j++) {
            String path = fileRoot + File.separator + serviceName + "_" + j + ".csv";
            files[j - 1] = new File(path);
        }
        File file = new File(fileRoot + File.separator + serviceName + ".csv");
        FileUtil.mergeFiles(file, files);
        try {
            // 下载到本地
            FileUtil.downloadCsvToLocal(file, serviceName, "csv", "utf-8", response);
        } catch (Exception e) {
            LogUtil.error(serviceName + ",error:" + e);
        } finally {
            // 下载完毕后，删除文件
            if (dir != null) {
                FileUtil.deleteDir(dir);
            }
            if(downloadExcelOperate!=null){
                    downloadExcelOperate.put(reqJson,System.currentTimeMillis());
                    session.setAttribute("downloadExcelOperate",downloadExcelOperate);
            }
            EXPORT.info("导出数据结束,reqJson:"+JSON.toJSONString(dsReqInfo));
        }

        return null;
     }
    
    /**
     * 准备带有网页授权功能的规则调用
     * 
     * @param request
     * @param response
     * @param repInfo
     * @param authInfo
     * @return
     * @throws InterruptedException
     */
	private Object prepareOauthInvoke(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo,
			AuthInfo authInfo) throws InterruptedException {
		Object cacheResult = null;
		String rootKey = MD5Util.MD5(repInfo.getParam());
		HttpStreamManager.set(new HttpCallStream(request, response));
		HttpStreamManager.setRootKey(rootKey);// 设置缓存Key（根Key，后面可能有N个相关的其他Key）
		String syncObjKey = rootKey + "-" + request.getSession().getId() + "-syncobj";
		authInfo = (AuthInfo) CachePool.getInstance().getCacheItem(syncObjKey);
		if (authInfo != null && authInfo.isAuthed() && authInfo.isNeedCacheResult()) {
			synchronized (authInfo) {
				if (authInfo.getServiceResult() == null) {
					authInfo.wait(60 * 1000); // 1分钟
				}
			}
			cacheResult = authInfo.getServiceResult();
			if (cacheResult != null) {
				// CachePool.getInstance().clearCacheItem(syncObjKey);
				LogUtil.debug("直接返回缓存结果：" + cacheResult);
			}
		} else {
			authInfo = new AuthInfo();
			CachePool.getInstance().putCacheItem(syncObjKey, authInfo);
		}
		return cacheResult;
	}
	/**
	 * 对于带有网页授权的规则，需要缓存处理结果
	 * @param authInfo
	 * @param result
	 */
	private void afterOauthInvoke(AuthInfo authInfo, Object result) {
		if (authInfo!=null && authInfo.isNeedCacheResult()) {
			LogUtil.warn("网页授权前挂起Service执行结果缓存："+result);
			synchronized (authInfo) {
				authInfo.setServiceResult(result);
				authInfo.notifyAll();
				LogUtil.warn("--After execute, notify other threads. Result..=" + result);
			}
		}
	}
}

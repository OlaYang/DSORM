package com.meiqi.openservice.action;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.dao.ITServiceDao;
import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.mushroom.entity.TMushroomTableService;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;
import com.meiqi.util.MyApplicationContextUtil;

@Service("clearCacheAction")
public class ClearCacheAction extends BaseAction{
    private static final Log LOG =  LogFactory.getLog("clearcache");
	@Autowired
	private IPushAction pushAction;
	@Autowired
	private ITServiceDao tServiceDao;
	@Autowired
	private ThreadHelper indexTheadHelper;
	@SuppressWarnings("unchecked")
	public String clear(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
		Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		String serName=paramMap.get("serviceName");
		
		if(!StringUtils.isEmpty(serName)){
			String[] serviceNames=serName.split(",");
			long st=System.currentTimeMillis();
			LOG.info("开始！clear cache 清除数据源总量："+serviceNames.length+"startCurrentTime:"+st);
			for(int i=0;i<serviceNames.length;i++){
				String serviceName=serviceNames[i];
				try {
					LOG.info("当前执行第"+(i+1)+"数据源，数据源名："+serviceName);		
					pushAction.updateService(serviceName);
				} catch (Exception e) {
					return "serviceName:"+serviceName+"清除缓存失败！:"+e.getMessage();
				}
			}
			long et=System.currentTimeMillis();
			LOG.info("完成！clear cache 清除数据源总量："+serviceNames.length+"startCurrentTime:"+et);
			LOG.info("clear cache 本次耗时ms："+(et-st));
			return "缓存清除成功！";
		}else{
			return "缓存清除失败，缺少参数！serviceName:"+serName;
		}
	}
	
	public String clearAll(List<TMushroomTableService> serList){
		int total=serList.size();
		LOG.info("开始！clear cache ALL 清除数据源缓存开始,总量："+total);
		if(0<total){
			List<String> tmpList=new ArrayList<String>();
			int size=100;
			for(int i=0;i<serList.size();i++){
			    tmpList.add(serList.get(i).getServiceName());
	             if(i!=0&&((i+1)%size==0)){
	                 //使用多线程做业务
	                 LOG.info("开始！clear cache  清除数据源缓存开始,当前批次总量："+tmpList.size());
	                 clearCacheThread thread=new clearCacheThread(tmpList);
	                 indexTheadHelper.execute(thread);
	                 tmpList=new ArrayList<String>();
	             }
	             if(i==total-1){
	                 //使用多线程做业务
	                 LOG.info("开始！clear cache  清除数据源缓存开始,当前批次总量："+tmpList.size());
	                 clearCacheThread thread=new clearCacheThread(tmpList);
	                 indexTheadHelper.execute(thread);
	                 tmpList=new ArrayList<String>();
	             }
	             //System.out.println("add serviceName to list index:"+i);
	             LOG.info("add serviceName to list index:"+i);
	        }
		}
		return "后台多线程缓存清除中！";
	}
	public String clearAll(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
	    ApplicationContext applicationContext=MyApplicationContextUtil.getContext();
	    LOG.info("-----------------开始！clear cache ALL begin---------------------");
	    long begin=System.currentTimeMillis();
	    tServiceDao=(ITServiceDao)applicationContext.getBean("tServiceDao");
	    indexTheadHelper=(ThreadHelper)applicationContext.getBean("indexTheadHelper");
	    //只查询出技术陈数据源，否则会重复重载基础数据源
		List<TService> serList= tServiceDao.getAllBaseTServiceInfo();
		long end=System.currentTimeMillis();
		long time=end-begin;
		LOG.info("-----------------开始！clear cache ALL begin query serviceName 耗时---------------------："+time);
		int total=serList.size();
		LOG.info("开始！clear cache ALL 清除数据源缓存开始,总量："+total);
		List<String> tmpList=new ArrayList<String>();
		int size=Integer.parseInt(com.meiqi.openservice.commons.config.SysConfig.getValue("clearCacheSize"));
		for(int i=0;i<serList.size();i++){
		    tmpList.add(serList.get(i).getName());
             if(i!=0&&((i+1)%size==0)){
                 //使用多线程做业务
                 LOG.info("开始！clear cache  清除数据源缓存开始,当前批次总量："+tmpList.size());
                 clearCacheThread thread=new clearCacheThread(tmpList);
                 indexTheadHelper.execute(thread);
                 tmpList=new ArrayList<String>();
             }
             if(i==total-1){
                 //使用多线程做业务
                 LOG.info("开始！clear cache  清除数据源缓存开始,当前批次总量："+tmpList.size());
                 clearCacheThread thread=new clearCacheThread(tmpList);
                 indexTheadHelper.execute(thread);
                 tmpList=new ArrayList<String>();
             }
             //System.out.println("add serviceName to list index:"+i);
             LOG.info("add serviceName to list index:"+i);
        }
		return "后台多线程缓存清除中！";
	}
	
	/**
	 * 定时清空DSORM的所有缓存和对应的memcached的缓存 
	* @Title: clearAllDSORMCache 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param   参数说明 
	* @return void    返回类型 
	* @throws
	 */
	public void clearAllDsormCache(){
	    clearAll(null, null, null);
    }
	
	
	class clearCacheThread implements ThreadCallback {
		private List<String>  serviceNames;
		public clearCacheThread(List<String>  serviceNames){
			super();
			this.serviceNames=serviceNames;
		}
		public void run() {
		    pushAction=(IPushAction)MyApplicationContextUtil.getBean("pushAction");
			for(String serviceName:serviceNames){
			    try { 
			        //System.out.println("clear cache  serviceNane:"+serviceName);
			        long begin=System.currentTimeMillis();
                    pushAction.updateService(serviceName);
                    long end=System.currentTimeMillis();
                    long time=end-begin;
                    //System.out.println("clear cache  serviceNane take time:"+time);
                    LOG.info("clear cache  serviceName:"+serviceName+",take time:"+time);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.info("clear cache error serviceNane:"+serviceName+",errorMsg:"+serviceName);
                }
			}
		}
	 }

	public String clearPageCache(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
        ResponseInfo respInfo = new ResponseInfo();

        String url = paramMap.get("url");
        String cdnObjectPath = paramMap.get("cdnObjectPath");
        String cachePort = paramMap.get("cachePort");
        String memcacheDomain=paramMap.get("memcacheDomain");
        if(StringUtils.isEmpty(memcacheDomain)){
            memcacheDomain="admmemcache.lejj.com";
        }

        if (StringUtils.isNotEmpty(url) && StringUtils.isEmpty(cachePort)) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("cachePort is empty. ");
            return JSON.toJSONString(respInfo);
        }

        StringBuffer sb = new StringBuffer();
        boolean result = false;
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);

        if (StringUtils.isNotEmpty(url)) {
            try {
                // 清除page 缓存
                MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcacheDomain+":" + cachePort));
                MemcachedClient memcachedClient = builder.build();
                Object object = memcachedClient.get(url);
                if (object != null) {
                    result = memcachedClient.delete(url);
                    LOG.info("clear page cache result:" + result + ",url:" + url);
                    sb.append("清理页面缓存" + (result==true?"成功":"失败") + ",路径:" + url + ". ");
                } else {
                    LOG.info("page cache not exist" + ",url:" + url);
                    sb.append("页面缓存不存在" + ",路径:" + url + ". ");
                }
    
            } catch (Exception e) {
                LOG.error("clear page cache fail, url:" + url + ",error:" + e);
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                sb.append("清理页面缓存失败, 路径:" + url + ",失败原因:" + e.getMessage());
            }
        }
        
        if (StringUtils.isNotEmpty(cdnObjectPath)) {
            if (sb.length() > 0) {
                sb.append(";");
            }
                // 清除cdn 缓存
                String clearCdnCache = "";
				try {
					clearCdnCache = clearCdnCache(cdnObjectPath);
				} catch (UnsupportedEncodingException e) {
					LOG.error("clearCdnCache error");
				}
                LOG.info("clear cdn cache result:"+clearCdnCache+",cdnObjectPath:" + cdnObjectPath);
                sb.append(clearCdnCache);
        }

        respInfo.setDescription(sb.toString());
        return JSON.toJSONString(respInfo);
    }
    
    //获取ACCESS_TOKEN请求的url
    private static final String ACCESS_TOKEN = "https://cdncs-api.fastweb.com.cn/oauth/access_token";
	
    //清理CDN缓存的请求地址
    private static final String CLEAR_CDN_URL = "https://cdncs-api.fastweb.com.cn/cont/add_purge.json"; 
    /**
     * 
     * 清除cdn 缓存
     *
     * @param cdnObjectPath
     * @return
     * @throws UnsupportedEncodingException 
     * @throws Exception 
     */
    private String clearCdnCache(String cdnObjectPath) throws UnsupportedEncodingException{
    	JSONObject jObject = new JSONObject();
    	jObject.put("grant_type", "client_credentials");
    	jObject.put("appid", "5dc4a521c2d698756b868898066ac4");
    	jObject.put("appsecret", "13bb6f77d5");
        String httpPostData = com.meiqi.dsmanager.util.HttpUtil.httpPostData(ACCESS_TOKEN, jObject.toJSONString(), 3000);
        httpPostData =new String(httpPostData.getBytes("ISO8859_1"),"utf-8").trim();
        JSONObject parseObject = JSONObject.parseObject(httpPostData);
        
        String status = parseObject.getString("status");
        String result = "";
        if("1".equals(status)){
        	String str = parseObject.getString("result");
        	JSONObject parseObject2 = JSONObject.parseObject(str);
        	String access_token = parseObject2.getString("access_token");
        	
        	JSONObject jObject1 = new JSONObject();
        	JSONObject jObject2 = new JSONObject();
        	jObject2.put("url_name",cdnObjectPath);
        	JSONArray array = new JSONArray();
        	array.add(jObject2);
        	jObject1.put("files", array);
        	jObject1.put("access_token", access_token);
        	String httpPostData2 = com.meiqi.dsmanager.util.HttpUtil.httpPostData(CLEAR_CDN_URL, jObject1.toJSONString(), 3000);
        	httpPostData2 =new String(httpPostData2.getBytes("ISO8859_1"),"utf-8").trim();
        	JSONObject parseObject1 = JSONObject.parseObject(httpPostData2);
        	String status1 = parseObject1.getString("status");
        	if("1".equals(status1)){
        		result = "清理CDN 缓存成功,路径:" + cdnObjectPath;
        	}else{
        		result = parseObject1.getString("info");
            	result = "清理CDN 缓存失败,原因:" + result;
        	}
        	
        }else{
        	result = parseObject.getString("info");
        	result = "清理CDN 缓存失败,原因:" + result;
        }
		return result;
    }



    
}

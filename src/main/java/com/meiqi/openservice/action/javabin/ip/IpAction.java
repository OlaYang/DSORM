/**   
* @Title: IpAction.java 
* @Package com.meiqi.openservice.action.javabin.ip 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年8月18日 上午11:30:18 
* @version V1.0   
*/
package com.meiqi.openservice.action.javabin.ip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.Constants;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.PinyinUtil;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.ListUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.javabin.ip.util.IPSeeker;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.IpverifyUtil;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @ClassName: IpAction 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年8月18日 上午11:30:18 
 *  
 */
@Service
public class IpAction extends BaseAction{

    @Autowired
    private IDataAction dataAction;
    
    private static final Log LOG =  LogFactory.getLog("ip");
    
    private  IPSeeker iPSeeker=new IPSeeker("qqwry.dat","util"+File.separator);  
    
    public Object getCountry(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
    
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        String  ip=map.get("ip").toString();
        String country=iPSeeker.getIPLocation(ip).getCountry(); 
        Map<String,String> result=new HashMap<String, String>();
        result.put("country", country);
        return result;
    }
    
    public Object getArea(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        String  ip=map.get("ip").toString();
        String area=iPSeeker.getIPLocation(ip).getArea(); 
        Map<String,String> result=new HashMap<String, String>();
        result.put("area", area);
        return result;
    }
    
    public static String getClientIP(HttpServletRequest request) {
        
        String ip = request.getHeader("Proxy-Client-IP");
        if(StringUtils.isEmpty(ip) || "127.0.0.1".equals(ip.trim())){
            ip=null;
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    public  String reloadIpFile(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        IPSeeker.ipCache.clear();
        IPSeeker.loadIpFile();
        return "success";
    }
    
    public  String reloadIpBlackFile(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        IpverifyUtil.reloadIpBlackFile();
        return "success";
    }
    
    public  Object getCountryAndArea(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        String ipAddress=getClientIP(request);
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        String  ip=map.get("ip")==null?"":map.get("ip").toString();
        if(StringUtils.isEmpty(ip)){
            ip=ipAddress;
        }
        LOG.info("ipAddress:"+ip);
        String[] ipArray=ip.split(",");
        int length=ipArray.length;
        if(length>1){
            ip=ipArray[0];
        }
        if(StringUtils.isNotEmpty(ip)){
            ip=ip.trim(); 
        }
        Map<String,String> result=new HashMap<String, String>();
        try {
            String country=iPSeeker.getIPLocation(ip).getCountry(); 
            String area=""; 
            String province="";
            String city="";
            int index=country.indexOf("省");
            int index1=country.indexOf("市");
            int index11=country.indexOf("州");
            if(index!=-1 && (index1!=-1 || index11!=-1)){
                if(index!=-1){
                    province=country.substring(0, index);
                }
                String tmp=country.substring(index+1);
                int index2=tmp.indexOf("市");
                int index22=tmp.indexOf("州");
                if(index2!=-1){
                    city=tmp.substring(0,index2);
                }else if(index22!=-1){
                    city=tmp.substring(0,index22);
                }
            }else if(index==-1 && index1!=-1){
                  province=country.substring(0,index1);
                  city=province;
            }
            if(StringUtils.isEmpty(province) || StringUtils.isEmpty(city)){
                if(length==3){
                    //如果是ip里面有三个ip，而且第一个没有取到信息，那么取第二个Ip去过去信息
                    ip=ipArray[1].trim();
                    country=iPSeeker.getIPLocation(ip).getCountry(); 
                    index=country.indexOf("省");
                    index1=country.indexOf("市");
                    if(index!=-1 && index1!=-1){
                        if(index!=-1){
                            province=country.substring(0, index);
                        }
                        String tmp=country.substring(index+1);
                        int index2=tmp.indexOf("市");
                        if(index2!=-1){
                            city=tmp.substring(0,index2);
                        }
                    }else if(index==-1 && index1!=-1){
                          //直辖市
                          province=country.substring(0,index1);
                          city=province;
                    }
                    if(StringUtils.isEmpty(province) || StringUtils.isEmpty(city)){
                        //LOG.info("当前的ip地址ipAddress1："+ipAddress+",没有定位成功,message:"+country);
                        LogUtil.error("当前的ip地址ipAddress1："+ipAddress+",没有定位成功,message:"+country);
                    }
                }else{
                   //LOG.info("当前的ip地址ipAddress2："+ipAddress+",没有定位成功,message:"+country);
                   LogUtil.error("当前的ip地址ipAddress2："+ipAddress+",没有定位成功,message:"+country);   
                   return "";
                }
            }
            //获取直辖市的区或者县
            if(index1+1 <= country.length()-1){
                int index4=country.indexOf("区");
                int index44=country.indexOf("县");
                if(index4!=-1){
                    area=country.substring(index1+1,index4);
                }else if(index44!=-1){
                    area=country.substring(index1+1,index44);
                }
            }
            result.put("province", province);
            result.put("city", city);
            result.put("citypinyin", PinyinUtil.getDefaultPinyin(city));
            if(StringUtils.isNotEmpty(area)){
                result.put("area", area);
            }
            
            if(StringUtils.isNotEmpty(city)){
                //根据城市名称获取简称
                String serviceName_esc_pay_log = "T_BUV1_region_short";
                DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
                serviceReqInfo.setServiceName(serviceName_esc_pay_log);
                Map<String,Object> param=new HashMap<String, Object>();
                param.put("region_name", city);
                serviceReqInfo.setParam(param);
                serviceReqInfo.setNeedAll("1");
                RuleServiceResponseData responseData = null;
                String data =dataAction.getData(serviceReqInfo,"");
                responseData = DataUtil.parse(data, RuleServiceResponseData.class);
                if(ListUtil.notEmpty(responseData.getRows())){
                        responseData.setCode(Constants.GetResponseCode.SUCCESS);
                }
                if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                    List<Map<String, String>> list=responseData.getRows();
                    if(ListUtil.notEmpty(list)){
                        Map<String, String> r=list.get(0);
                        String region_short=r.get("region_short");
                        String region_id=r.get("region_id");
                        result.put("region_short", region_short);
                        result.put("region_id", region_id);
                    }
                } 
            }
            
                Cookie provinceCookie = new Cookie("province",URLEncoder.encode(province,"UTF-8"));
                provinceCookie.setPath("/");
                response.addCookie(provinceCookie);
                
                Cookie cityCookie=new Cookie("city",URLEncoder.encode(city,"UTF-8"));
                cityCookie.setPath("/");
                response.addCookie(cityCookie);
            } catch (Exception e) {
                LOG.info("ipAction ipAddress:"+ip+",error:"+e);
                return "";
            }
        return result;
    }
    
    /**
     * 按标准报文输出的ip接口
    * @Title: getIpMessage 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return Object    返回类型 
    * @throws
     */
    public  Object getIpMessage(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        Map<String,String> result=(Map<String,String>)getCountryAndArea(request, response, repInfo);
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        if(result!=null){
            list.add(result);
        }
        respInfo.setRows(list);
        return respInfo;
    }
    
    
    public Object getIpMsg(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        Map<String,String> result=new HashMap<String, String>();
        String  ip=map.get("ip").toString();
        String resout = "";
        try{
         String str = getJsonContent("http://ip.taobao.com/service/getIpInfo.php?ip="+ip);
         JSONObject obj = JSONObject.fromObject(str);
         JSONObject obj2 =  (JSONObject) obj.get("data");
         String code = obj.get("code").toString();
         if(code.equals("0")){
          result.put("country", obj2.get("country").toString());
          result.put("area", obj2.get("area").toString());
          result.put("province", obj2.get("region").toString());
          result.put("city", obj2.get("city").toString());
          result.put("isp", obj2.get("isp").toString());
         }else{
             result.put("error", "IP地址有误");
         }
        }catch(Exception e){
            resout = "获取IP地址异常："+e.getMessage();
            result.put("error", resout);
        }
        return result;

    }
    
    public static String getJsonContent(String urlStr)
    {
        try
        {// 获取HttpURLConnection连接对象
            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url
                    .openConnection();
            // 设置连接属性
            httpConn.setConnectTimeout(3000);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("GET");
            // 获取相应码
            int respCode = httpConn.getResponseCode();
            if (respCode == 200)
            {
                return ConvertStream2Json(httpConn.getInputStream());
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    private static String ConvertStream2Json(InputStream inputStream)
    {
        String jsonStr = "";
        // ByteArrayOutputStream相当于内存输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        // 将输入流转移到内存输出流中
        try
        {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, len);
            }
            // 将内存流转换为字符串
            jsonStr = new String(out.toByteArray());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonStr;
    }
    
}

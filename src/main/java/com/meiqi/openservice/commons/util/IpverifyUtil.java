/**   
* @Title: IpverifyUtil.java 
* @Package com.meiqi.openservice.commons.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年11月19日 下午1:00:21 
* @version V1.0   
*/
package com.meiqi.openservice.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.data.util.LogUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.javabin.ip.IpAction;

/** 
 * @ClassName: IpverifyUtil 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年11月19日 下午1:00:21 
 *  
 */
public class IpverifyUtil {

    static String path = BaseAction.basePath + File.separator + "lejj_resource" + File.separator + "ip"+ File.separator +"ipblack.properties";
    public static Properties properties = new Properties();
    static{
        properties = new Properties();
            try {
                properties.load(new FileInputStream(new File(path)));
            } catch (Exception e) {
                LogUtil.error("verifyCurrentIp error:"+e);
            }
    }
    
    
    public static void reloadIpBlackFile(){
        properties = new Properties();
            try {
                properties.load(new FileInputStream(new File(path)));
            } catch (Exception e) {
                LogUtil.error("verifyCurrentIp error:"+e);
            }
    }
    
    public static  boolean verifyCurrentIp(HttpServletRequest request, HttpServletResponse response,String content){
        
        String ipverifyopen=properties.getProperty("ipverifyopen");
        if("1".equals(ipverifyopen)){
            if(request!=null){
                String ip=IpAction.getClientIP(request);
                if(StringUtils.isNotEmpty(ip)){
                    String blackip="";
                    try {
                        blackip=properties.getProperty("ip");
                        LogUtil.info("verifyCurrentIp current ip:"+ip +",blackip :"+blackip+",content:"+content);
                        if(StringUtils.isNotEmpty(blackip)){
                            String[] ipArray=ip.split(",");
                            for(String ipTmp:ipArray){
                                if(blackip.contains(ipTmp.trim())){
                                    LogUtil.error("verifyCurrentIp current ip:"+ip +",blackip :"+blackip+",content:"+content);
                                    return false;
                                }
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.error("verifyCurrentIp current ip:"+ip +",blackip :"+blackip+",content:"+content+",error:"+e);
                    }
                }
            }
        }else{
            return true;
        }
        return true;
    }
    
}

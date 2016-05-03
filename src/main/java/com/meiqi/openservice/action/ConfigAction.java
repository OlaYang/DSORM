package com.meiqi.openservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.RsaKeyTools;
import com.meiqi.openservice.commons.util.VerifySqlAttackUtil;

/**
 * 
* @ClassName: ConfigAction 
* @Description: TODO(这里用一句话描述这个类的作用) 刷新配置相关信息
* @author zhouyongxiong
* @date 2015年12月9日 下午2:49:17 
*
 */
@Service
public class ConfigAction extends BaseAction{
    
    /**
     * 刷新rsa相关配置信息
    * @Title: reloadRsaConfig 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public String reloadRsaConfig(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        RsaKeyTools.reloadRsaConfig();
        return "success";
    }
    
    /**
     * 刷新extraSysConfig.properties配置文件信息
    * @Title: reloadSystemExtraConfig 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public String reloadSystemConfig(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        SysConfig.readProperties();
        com.meiqi.openservice.commons.config.SysConfig.readProperties();
        com.meiqi.util.SysConfig.readProperties();
        return "success";
    }
    
    public String reloadVerifySqlAttackConfig(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        VerifySqlAttackUtil.reloadVerifySqlAttackConfig();
        return "success";
    }
}

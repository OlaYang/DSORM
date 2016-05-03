package com.meiqi.openservice.action.javabin.experience;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.javabin.ip.IpAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.DataUtil;
/**
 * 
* @ClassName: ExperiencePavilionAction 
* @Description: TODO(获取体验馆信息) 
* @author fangqi
* @date 2015年9月17日 上午10:08:18 
*
 */
@Service
public class ExperiencePavilionAction extends BaseAction {
    
    @Autowired
    private IDataAction dataAction;
    
    private String HTTP_BIND_CORS_ALLOW_ORIGIN = SysConfig.getValue("webchat.httpBindCrosAllowOrigin");
    
    /**
     * 
    * @Title: getExperienceInfo 
    * @Description: 根据城市信息和站点信息获取体验馆信息
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    @SuppressWarnings("unchecked")
    public String getExperienceInfo(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
        response.setHeader("Access-Control-Allow-Origin", HTTP_BIND_CORS_ALLOW_ORIGIN);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        Map<String,Object> reqMap = DataUtil.parse(repInfo.getParam());
        
        // 如果传入城市，则获取城市体验馆信息
        String city = null;
        if(reqMap.containsKey("city")){
            city = String.valueOf(reqMap.get("city"));
        }
        
        // 如果未传入城市或传入全国，则获取ip对应城市体验馆信息
        if(StringUtils.isBlank(city) || city.equals("全国")){
            Object obj = new IpAction().getCountryAndArea(request, response, repInfo);//根据IP获取当前城市信息
            Map<String,Object> cityMap = obj.equals("")?null:(Map<String, Object>)obj ;
            if(null!=cityMap && cityMap.containsKey("city")){
                city = String.valueOf(cityMap.get("city"));
            }
        }
        
        String result = "";
        if(!StringUtils.isBlank(city)){
            String site_id = reqMap.containsKey("site_id") ? String.valueOf(reqMap.get("site_id")) : "0";
            LogUtil.info("HMJ_HSV1_StoreInfo:获取site_id="+site_id+"和city_name="+city+"的体验馆信息！");
            dsReqInfo.setServiceName("HMJ_HSV1_StoreInfo");//根据城市名称获取体验馆信息
            reqMap.put("city_name", city);//获取城市名称
            reqMap.put("site_id", site_id);
            dsReqInfo.setNeedAll("1");
            dsReqInfo.setParam(reqMap);
            result = dataAction.getData(dsReqInfo);
            LogUtil.info("HMJ_HSV1_StoreInfo:体验馆信息-->"+result);
        }else{
            ResponseInfo resqInfo = new ResponseInfo();
            resqInfo.setCode("1");
            resqInfo.setDescription("城市信息为空！");
            result = JSON.toJSONString(resqInfo);
            LogUtil.info("Experience_Pavilion_Info:城市信息为空！");
        }
        return result;
    }
}

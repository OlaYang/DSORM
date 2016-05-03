package com.meiqi.app.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.ShopConfigService;

/**
 * 
 * @ClassName: ShopConfigController
 * @Description:app 配置
 * @author 杨永川
 * @date 2015年4月28日 下午9:06:28
 *
 */
// @Controller
// @RequestMapping
@Service
public class ShopConfigAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(ShopConfigAction.class);
    @Autowired
    private ShopConfigService   shopConfigService;



    public ShopConfigService getShopConfigService() {
        return shopConfigService;
    }



    public void setShopConfigService(ShopConfigService shopConfigService) {
        this.shopConfigService = shopConfigService;
    }



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        if (StringUtils.isBlank(url)) {
            String method = appRepInfo.getMethod();
            if ("get".equals(method)) {
                return getResource();
            }
        } else if (url.contains("clearSiteCache")) {
            return clearSiteCache();
        } else if (url.contains("clearShopConfigCache")) {
            return clearShopConfigCache();
        } else if (url.contains("clearAndReadProperties")) {
            return clearAndReadProperties();
        }

        return null;
    }



    /**
     * 
     * @Title: clearShopConfigCache
     * @Description:清除app config 缓存
     * @param @return
     * @return String
     * @throws
     */
    // @ResponseBody
    // @RequestMapping(value = ContentUtils.VERSION + "/clearShopConfigCache",
    // method = RequestMethod.GET)
    private String clearShopConfigCache() {
        setShopConfig();
        return JsonUtils.getSuccessJson(null);
    }



    /**
     * 
     * @Title: clearShopConfigCache
     * @Description:清除Site config 缓存
     * @param @return
     * @return String
     * @throws
     */
    // @ResponseBody
    // @RequestMapping(value = ContentUtils.VERSION + "/clearSiteCache", method
    // = RequestMethod.GET)
    private String clearSiteCache() {
        shopConfigService.clearSiteCache();
        return JsonUtils.getSuccessJson(null);
    }



    /**
     * 
     * @Title: getResource
     * @Description:获取资源
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    // @ResponseBody
    // @RequestMapping(value = ContentUtils.VERSION, method = RequestMethod.GET)
    private String getResource(/* HttpServletRequest request */) {
        LOG.info("Function:getResource.Start.");
        // Map<String, String> resourceMap = shopConfigService.getResource();
        // String resourceJson = JsonUtils.objectFormatToString(resourceMap);
        String appApiJson = JsonUtils.readJsonFile(BaseAction.basePath + "/lejj_resource/json/appApi.json");
        LOG.info("Function:getResource.End.");
        return appApiJson;
    }



    /**
     * 
     * @Title: clearAndReadProperties
     * @Description:清空proprties 并再次读入（用于配置了新的property）
     * @param @return
     * @return String
     * @throws
     */
    // @ResponseBody
    // @RequestMapping(value = ContentUtils.VERSION + "/clearAndReadProperties",
    // method = RequestMethod.GET)
    private String clearAndReadProperties() {
        LOG.info("Function:clearAndReadProperties.Start.");
        AppSysConfig.clearAndReadProperties();
        LOG.info("Function:clearAndReadProperties.End.");
        return JsonUtils.getSuccessJson(null);
    }

}

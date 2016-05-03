package com.meiqi.app.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.RegionService;
import com.meiqi.dsmanager.cache.CachePool;

/**
 * 
 * @ClassName: RegionController
 * @Description:
 * @author 杨永川
 * @date 2015年4月10日 下午1:24:07
 *
 */
@Service
public class RegionAction extends BaseAction {
    private static final Logger LOG                  = Logger.getLogger(RegionAction.class);
    private static final String REGIONJSONPROPERTY   = "regionId,regionName,headChar,subRegionList,regionType";
    private static final String APP_REGION_CACHE_KEY = "app_region_cache_key";
    @Autowired
    private RegionService       regionService;



    public RegionService getRegionService() {
        return regionService;
    }



    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        if (url.contains("allCity")) {
            return getAllCity();
        } else if (StringUtils.matchByRegex(url, "^region\\/\\d+$")) {
            Long parentId = StringUtils.getNumbers(url);
            return getRegionList(parentId);
        } else if (url.contains("setHeadChar")) {
            return setHeadChar();
        } else if (url.contains("hotCity")) {
            return getHotCity();
        } else if (url.contains("region/all")) {
            return getAllRegion();
        } else if (url.contains("clearAllRegion")) {
            return clearAllRegion();
        }

        return null;
    }



    /**
     * 
     * @Title: getAllRegion
     * @Description:获取所有的region 中国除外
     * @param @return
     * @return String
     * @throws
     */
    private String getAllRegion() {
        LOG.info("Function:getAllRegionList.Start.");
        String regionListJson = (String) CachePool.getInstance().getCacheItem(APP_REGION_CACHE_KEY);
        if (StringUtils.isBlank(regionListJson)) {
            List<Region> regionList = regionService.getAllRegion();
            regionListJson = JsonUtils.listFormatToString(regionList,
                    StringUtils.getStringList(REGIONJSONPROPERTY, ContentUtils.COMMA));
            CachePool.getInstance().putCacheItem(APP_REGION_CACHE_KEY, regionListJson);
        }
        LOG.info("Function:getAllRegionList.End.");
        return regionListJson;
    }



    /**
     * 
     * @Title: getRegionList
     * @Description:获取区域
     * @param @param regionId
     * @return void
     * @throws
     */
    private String getRegionList(long parentId) {
        LOG.info("Function:getRegionList.Start.");
        LOG.info("get region,parent region:id=" + parentId);
        List<Region> regionList = regionService.getRegionList(parentId);
        String regionListJson = JsonUtils.listFormatToString(regionList,
                StringUtils.getStringList(REGIONJSONPROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getRegionList.End.");
        return regionListJson;
    }



    /**
     * 
     * @Title: setHeadChar
     * @Description:
     * @param @param response
     * @param @return
     * @return String
     * @throws
     */
    private String setHeadChar() {
        LOG.info("Function:setheadChar.Start.");
        regionService.setHeadChar();
        LOG.info("Function:setheadChar.End.");
        return "redirect:/region/1";
    }



    /**
     * 
     * @Title: getHotRegion
     * @Description:获取热门城市
     * @param @param response
     * @return void
     * @throws
     */
    private String getHotCity() {
        LOG.info("Function:getHotCity.Start.");
        List<Region> hotCityList = regionService.getHotCity();
        String hotCityListJson = JsonUtils.listFormatToString(hotCityList,
                StringUtils.getStringList(REGIONJSONPROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getHotCity.End.");
        return hotCityListJson;
    }



    /**
     * 
     * @Title: getAllCity
     * @Description:获取热门城市
     * @param @param response
     * @return void
     * @throws
     */
    private String getAllCity() {
        LOG.info("Function:getAllCity.Start.");
        Map<String, List<Region>> cityMap = regionService.getAllCity();
        String cityMapJson = JsonUtils.objectFormatToString(cityMap);
        LOG.info("Function:getAllCity.End.");
        return cityMapJson;
    }



    /**
     * 
     * @Title: clearAllRegion
     * @Description: 清除缓存并重新获取 存入缓存
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    private String clearAllRegion() {
        LOG.info("Function:clearAllRegion.Start.");
        CachePool.getInstance().removeCacheItem(APP_REGION_CACHE_KEY);
        String allRegion = getAllRegion();
        LOG.info("Function:clearAllRegion.End.");
        return allRegion;
    }
}

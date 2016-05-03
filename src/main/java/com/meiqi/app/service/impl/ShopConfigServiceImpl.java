package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.dao.ShopConfigDao;
import com.meiqi.app.pojo.ShopConfig;
import com.meiqi.app.service.ShopConfigService;

@Service
public class ShopConfigServiceImpl implements ShopConfigService {
    private static final Logger LOG = Logger.getLogger(ShopConfigServiceImpl.class);
    Class<ShopConfig>           cls = ShopConfig.class;
    
    @Autowired
    private ShopConfigDao       shopConfigDao;



    public ShopConfigDao getShopConfigDao() {
        return shopConfigDao;
    }



    public void setShopConfigDao(ShopConfigDao shopConfigDao) {
        this.shopConfigDao = shopConfigDao;
    }



    /**
     * 
     * @Title: getAppShopConfig
     * @Description:
     * @param @return
     * @throws
     */
    @Override
    public Map<String, String> getAppShopConfig() {
        LOG.info("Function:getAppShopConfig.Start.");
        List<ShopConfig> shopConfigList = shopConfigDao.getAppShopConfig(cls, ContentUtils.SHOPCONFIG_CODE_APPCONFIG);
        Map<String, String> result = assembleShopConfig(shopConfigList);
        LOG.info("Function:getAppShopConfig.End.");
        return result;
    }



    /**
     * 
     * @Title: assembleShopConfig
     * @Description:将shopConfig 对象 分装为map
     * @param @param shopConfigList
     * @param @return
     * @return Map<String,String>
     * @throws
     */
    public Map<String, String> assembleShopConfig(List<ShopConfig> shopConfigList) {
        Map<String, String> result = new HashMap<String, String>();
        if (CollectionsUtils.isNull(shopConfigList)) {
            return result;
        }
        for (ShopConfig shopConfig : shopConfigList) {
            result.put(shopConfig.getCode(), shopConfig.getValue());
        }
        return result;
    }



    /**
     * 
     * @Title: clearSiteCache
     * @Description:清除Site config 缓存
     * @param
     * @throws
     */
    @Override
    public void clearSiteCache() {
        LOG.info("Function:clearSiteCache.Start.");
        shopConfigDao.clearSiteCache();
        LOG.info("Function:clearSiteCache.End.");
    }



    /**
     * 
     * @Title: getResource
     * @Description:获取app 资源配置
     * @param @return
     * @throws
     */
    @Override
    public Map<String, String> getResource() {
        LOG.info("Function:getResource.Start.");
        List<ShopConfig> shopConfigList = shopConfigDao.getAppShopConfig(cls, ContentUtils.SHOPCONFIG_CODE_APPRESOURCE);
        Map<String, String> result = assembleShopConfig(shopConfigList);
        LOG.info("Function:getResource.End.");
        return result;
    }

}

package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.RegionDao;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.service.RegionService;

/**
 * 
 * @ClassName: RegionServiceImpl
 * @Description:Region
 * @author 杨永川
 * @date 2015年4月10日 下午2:19:03
 *
 */
@Service
public class RegionServiceImpl implements RegionService {
    private static final Logger LOG = Logger.getLogger(RegionServiceImpl.class);
    Class<Region>               cls = Region.class;

    @Autowired
    private RegionDao           regionDao;



    public RegionDao getRegionDao() {
        return regionDao;
    }



    public void setRegionDao(RegionDao regionDao) {
        this.regionDao = regionDao;
    }



    /**
     * 
     * @Title: getRegionList
     * @Description:获取区域
     * @param @param regionId
     * @param @return
     * @throws
     */
    @Override
    public List<Region> getRegionList(long parentId) {
        LOG.info("Function:getRegionList.Start.");
        List<Region> regionList = regionDao.getRegionList(cls, parentId);
        LOG.info("Function:getRegionList.End.");
        return regionList;
    }



    /**
     * 
     * @Title: setHeadChar
     * @Description:设置区域名字首字母
     * @param @param cls
     * @throws
     */
    @Override
    public void setHeadChar() {
        LOG.info("Function:setHeadChar.Start.");
        regionDao.setHeadChar(cls);
        LOG.info("Function:setHeadChar.End.");
    }



    /**
     * 
     * @Title: getHotCityList
     * @Description:获取热门城市
     * @param @return
     * @throws
     */
    @Override
    public List<Region> getHotCity() {
        LOG.info("Function:getHotCity.Start.");
        List<Region> hotcityList = regionDao.getHotCity(cls);
        LOG.info("Function:getHotCity.End.");
        return hotcityList;
    }



    /**
     * 
     * @Title: getCityList
     * @Description:获取所有城市，按照首字母分类
     * @param @return
     * @throws
     */
    @Override
    public Map<String, List<Region>> getAllCity() {
        LOG.info("Function:getAllCity.Start.");
        Map<String, List<Region>> cityMap = assembleCity(regionDao.getAllCity(cls));
        LOG.info("Function:getAllCity.End.");
        return cityMap;
    }



    /**
     * 
     * @Title: assembleCity
     * @Description:按照首字母分类 城市
     * @param @param cityList
     * @param @return
     * @return Map
     * @throws
     */
    public Map<String, List<Region>> assembleCity(List<Region> cityList) {
        if (CollectionsUtils.isNull(cityList)) {
            LOG.info("get city is null.");
            return null;
        }
        Map<String, List<Region>> cityMap = new LinkedHashMap<String, List<Region>>();
        for (Region region : cityList) {
            String headChar = region.getHeadChar();
            if (cityMap.containsKey(headChar) && null != cityMap.get(headChar)) {
                cityMap.get(headChar).add(region);
            } else {
                ArrayList<Region> list = new ArrayList<Region>();
                list.add(region);
                cityMap.put(headChar, list);
            }
        }
        return cityMap;
    }



    /**
     * 
     * @Title: getAllRegionList
     * @Description:获取所有的region 中国除外
     * @param @return
     * @throws
     */
    @Override
    public List<Region> getAllRegion() {
        LOG.info("Function:getAllRegionList.Start.");
        List<Region> regionList = regionDao.getAllRegion(cls);
        LOG.info("Function:getAllRegionList.End.");
        return regionList;
    }

}

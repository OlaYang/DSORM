package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Region;

public interface RegionDao extends BaseDao {

    List<Region> getAllRegion(Class<Region> cls);



    List<Region> getRegionList(Class<Region> cls, long parentId);



    List<Region> getHotCity(Class<Region> cls);



    List<Region> getAllCity(Class<Region> cls);



    void setHeadChar(Class<Region> cls);



    List<Region> getRegionList(Class<Region> cls, List<Long> regionIdList);



    Region getLinkedRegionByRegion(Region region);



    String getLinkedRegionByRegion(long regionId);



    Region getRegionByRegionId(long regionId);



    Region getRegionBySelRegionId(long regionId);

}

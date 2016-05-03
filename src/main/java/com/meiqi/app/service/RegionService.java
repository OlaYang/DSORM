package com.meiqi.app.service;

import java.util.List;
import java.util.Map;

import com.meiqi.app.pojo.Region;

public interface RegionService {

    List<Region> getAllRegion();



    List<Region> getRegionList(long parentId);



    List<Region> getHotCity();



    Map<String, List<Region>> getAllCity();



    void setHeadChar();
}

package com.meiqi.app.service;

import java.util.Map;

public interface ShopConfigService {

    Map<String, String> getAppShopConfig();



    void clearSiteCache();



    Map<String, String> getResource();
}

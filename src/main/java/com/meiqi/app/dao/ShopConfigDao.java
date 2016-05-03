package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.ShopConfig;

public interface ShopConfigDao extends BaseDao {
    List<ShopConfig> getAppShopConfig(Class<ShopConfig> cls, String code);
}

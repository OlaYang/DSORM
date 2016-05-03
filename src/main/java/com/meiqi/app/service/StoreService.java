package com.meiqi.app.service;

import java.util.Map;

import com.meiqi.app.pojo.Store;

public interface StoreService {

    Store getStoreByStoreId(long goodsId);



    boolean sendStoreInfo(long userId, String phone, String msg, Map<String, Object> param);
}

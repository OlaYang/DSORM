package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.CollectGoods;

public interface CollectGoodsDao extends BaseDao {
    List<CollectGoods> getAllCollectGoods(Class<CollectGoods> cls, long userId, int firstResult, int maxResults);

}

package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.CollectGoods;
import com.meiqi.app.pojo.Goods;

public interface CollectGoodsService {
    List<CollectGoods> getAllCollectGoods(long userId, int pageIndex, int pageSize);



    Goods hasGoods(Goods goods);



    boolean addCollectGoods(CollectGoods collectGoods);



    CollectGoods hasCollectGoods(long userId, long goodsId);



    CollectGoods hasCollectGoods(long favoriteId);



    boolean deleteCollectGoods(long userId, long goodsId);



    boolean deleteCollectGoods(long userId, String[] goodsIds);



    /**
     * 
     * 获取收藏商品总数
     *
     * @param userId
     * @return
     */
    int getCollectGoodsTotal(long userId);
}
package com.meiqi.app.service;

import com.meiqi.app.pojo.Shop;

public interface ShopService {
    /**
     * 
     * 获取商家信息，根据商品id
     *
     * @param goodsId
     * @return
     */
    Shop getShopByGoodsId(long goodsId, int cityId);
}

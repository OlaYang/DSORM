package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsAttribute;
import com.meiqi.app.pojo.OrderdGoodsStandard;

public interface GoodsService {

    List<Goods> getHotGoodsList(int pageIndex, int pageSize);



    List<Goods> getGoodsByCatId(long catId, int pagenumber, int count);



    Goods getGraphicDetail(long goodsId, String plat);



    Goods getGraphicDetailForIpad(long goodsId, String plat);



    String getGoodsDetailStyle(String plat);



    List<OrderdGoodsStandard> getGoodsStandards(long goodsId);



    long getGoodsIdByAttr(long goodsId, List<GoodsAttribute> selGoodsAttributeList);



    Goods getGoodsBaseInfo(long goodsId, int regionId);



    Goods getGoodsDynamicInfo(long goodsId, long userId);



    String getGoodsSn(long goodsId);

}

package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsAttr;

public interface GoodsAttrDao extends BaseDao {

    List<GoodsAttr> getFilterAttr(Class<GoodsAttr> cls, Long[] goodsIdArray, Long[] attrIdArray);



    List<GoodsAttr> getGoodsAttrByAttrId(Class<GoodsAttr> cls, List<Long> goodsAttrIdList);



    List<Long> getGoodsIdByAttIdAndAttrVal(Class<GoodsAttr> cls, long attrId, String attrValue);



    List<GoodsAttr> getGoodsStandardByGoodsId(Class<GoodsAttr> cls, long goodsId);



    List<GoodsAttr> getGoodsStandardByGoodsId(Class<GoodsAttr> cls, List<Long> sameGoodsIdList);



    long getGoodsByIdAndSelAttrId(Class<Goods> cls, List<Long> samegoodsIdList, List<Long> samegoodsIdList2);



    GoodsAttr getGoodsAttr(Class<GoodsAttr> cls, long goodsId, long goodsAttId);
}

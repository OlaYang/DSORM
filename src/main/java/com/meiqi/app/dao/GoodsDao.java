package com.meiqi.app.dao;

import java.util.List;
import java.util.Set;

import com.meiqi.app.pojo.Goods;

/**
 * 
 * @ClassName: ProductDao
 * @Description:
 * @author 杨永川
 * @date 2015年3月27日 下午3:55:30
 *
 */
public interface GoodsDao extends BaseDao {
    List<Goods> getHostGoodsList(Class<Goods> cls);



    List<Goods> getGoodsByCatId(Class<Goods> cls, long category_id, int pageNumber, int count);



    Goods getGraphicDetail(Class<Goods> cls, long goodsId);



    // List<Goods> getGoodsBySearch(Class<Goods> cls, int type, Set<Long>
    // goodsIdSet, String filterHql, String keyWord,
    // int sort);

    String getGoodsStyle(long goodsId);



    List<Goods> getRecommandGoods(long goodsId, long catId, String goodsStyle);



    List<Goods> getGoodsByGoodsIdList(List<Long> goodsIdList);



    String getCover(long goodsId);

}

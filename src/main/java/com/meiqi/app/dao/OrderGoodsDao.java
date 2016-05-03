package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.OrderGoods;

public interface OrderGoodsDao extends BaseDao {
    int getSoldAmountByGoodsId(Class<OrderGoods> cls, long goodsId);



    List<OrderGoods> getGoodsByOrderId(long orderId);
}

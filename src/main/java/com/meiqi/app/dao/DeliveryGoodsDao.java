package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.DeliveryGoods;

public interface DeliveryGoodsDao extends BaseDao {

    List<DeliveryGoods> getDeliveryGoodsByDeliveryId(long deliveryId);

}

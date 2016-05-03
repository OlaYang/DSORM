package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.DeliveryOrder;

public interface DeliveryOrderDao extends BaseDao {

    List<DeliveryOrder> getDeliveryOrderByOrderId(long orderId);

}

package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Order;

public interface OrderDao extends BaseDao {

    List<Order> getAllOrderByUserId(long userId, int firstResult, int maxResults);



    int getOrderTotalUserId(long userId);



    Order getOrderByUserIdAndOrderId(long userId, long orderId);

    Order getOrderByUserIdAndOrderId(long orderId);


    Order getOrderByOrderSn(Class<Order> cls, String orderSn);



    List<Order> getAllOrderByPhone(String phone, int firstResult, int maxResults);



    int getOrderTotalByPhone(String phone);
}

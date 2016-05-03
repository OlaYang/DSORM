package com.meiqi.app.service;

import com.meiqi.app.pojo.DiscountInfo;
import com.meiqi.app.pojo.Order;
import com.meiqi.app.pojo.PayStatus;
import com.meiqi.app.pojo.checkout.CodTradeReqDTO;

public interface OrderService {
    Order addGoodsToOrder(Order order);



    String getAllOrder(long userId, String plat, int pageIndex, int pageSize);



    String getOrderDetail(long userId, long orderId, String plat);



    boolean updatePayInfoByYeePay(CodTradeReqDTO codTradeReqDTO);



    String getpayWay(long orderId, long userId, String plat,String ip,String userAgent);



    PayStatus getPayStatus(long userId, long orderId, String platString);



    boolean cancelOrder(long orderId, long userId);



    boolean confirmShipping(long orderId, long userId);



    boolean deleteOrder(long orderId, long userId);



    DiscountInfo getDiscountInfo(DiscountInfo discountParam);



    String getAllOrderByPhone(String phone, int pageIndex, int pageSize, String plat);

}

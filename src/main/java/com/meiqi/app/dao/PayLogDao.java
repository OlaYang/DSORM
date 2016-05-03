package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.PayLog;

public interface PayLogDao extends BaseDao {

    List<PayLog> getPayLogByOrderId(Class<PayLog> cls, long orderId);



    double getPayedOrderAmountByOrderId(long orderId);

}

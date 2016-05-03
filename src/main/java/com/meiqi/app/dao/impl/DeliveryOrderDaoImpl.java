package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.DeliveryOrderDao;
import com.meiqi.app.pojo.DeliveryOrder;

@Service
public class DeliveryOrderDaoImpl extends BaseDaoImpl implements DeliveryOrderDao {

    @Override
    public List<DeliveryOrder> getDeliveryOrderByOrderId(long orderId) {

        String hql = "from DeliveryOrder DO where DO.orderId= ? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, orderId);
        return query.list();
    }

}

package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.dao.PayLogDao;
import com.meiqi.app.pojo.PayLog;
@Service
public class PayLogDaoImpl extends BaseDaoImpl implements PayLogDao {

    /**
     * 
     * @Title: getPayLogByOrderId
     * @Description:获取支付记录
     * @param @param cls
     * @param @param orderId
     * @param @return
     * @throws
     */
    @Override
    public List<PayLog> getPayLogByOrderId(Class<PayLog> cls, long orderId) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("orderId", orderId)).addOrder(Order.desc("addTime"));
        return criteria.list();
    }



    /**
     * 
     * @Title: getPayedOrderAmountByOrderId
     * @Description:获取订单已经支付的金额
     * @param @param orderId
     * @param @return
     * @throws
     */
    @Override
    public double getPayedOrderAmountByOrderId(long orderId) {
        String hql = "select sum(PL.orderAmount) from PayLog PL where PL.orderId = ? group by PL.orderId";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, orderId);
        List<Double> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            list.get(0);
        }
        return 0;
    }

}

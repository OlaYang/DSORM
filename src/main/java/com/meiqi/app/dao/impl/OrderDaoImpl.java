package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.OrderDao;
import com.meiqi.app.pojo.Order;

/**
 * 
 * @ClassName: OrderDaoImpl
 * @Description:
 * @author 杨永川
 * @date 2015年5月8日 下午6:18:24
 *
 */
@Service
public class OrderDaoImpl extends BaseDaoImpl implements OrderDao {

    /**
     * 
     * @Title: getAllOrderByUserId
     * @Description:获取订单 根据用户id
     * @param @param userId
     * @param @param lastMonthDayTime
     * @param @return
     * @throws
     */
    @Override
    public List<Order> getAllOrderByUserId(long userId, int firstResult, int maxResults) {
        String hql = "from Order O where O.userId = ? and O.isDel = 0 order by O.addTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }



    @Override
    public int getOrderTotalUserId(long userId) {
        String hql = "select count(*) from Order O where O.userId = ? and O.isDel = 0";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        List list = query.list();
        if (!CollectionsUtils.isNull(list) && null != list.get(0)) {
            return StringUtils.StringToInt(list.get(0).toString());
        }
        return 0;
    }


    
    @Override
    public Order getOrderByUserIdAndOrderId(long orderId) {
        String hql = "from Order O where O.orderId = ?  and O.isDel = 0 ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, orderId);
        List<Order> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Order getOrderByUserIdAndOrderId(long userId, long orderId) {
        String hql = "from Order O where O.userId = ? and O.orderId = ?  and O.isDel = 0 ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        query.setParameter(1, orderId);
        List<Order> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getOrderByOrderSn
     * @Description:根据订单号获取订单
     * @param @param cls
     * @param @param orderSn
     * @param @return
     * @throws
     */
    @Override
    public Order getOrderByOrderSn(Class<Order> cls, String orderSn) {

        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("orderSn", orderSn)).add(Restrictions.eq("isDel", 1));
        List<Order> orderList = criteria.list();
        if (!CollectionsUtils.isNull(orderList)) {
            return orderList.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getAllOrderByPhone
     * @Description:根据电话号码获取订单
     * @param @param phone
     * @param @param lastMonthDayTime
     * @param @return
     * @throws
     */
    @Override
    public List<Order> getAllOrderByPhone(String phone, int firstResult, int maxResults) {
        String hql = "from Order O where O.phone = ? and O.isDel = 0 order by O.addTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, phone);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }



    /**
     * 
     * @Title: getOrderTotalByPhone
     * @Description:根据电话号码获取订单总数
     * @param @param phone
     * @param @param phone
     * @param @return
     * @throws
     */
    @Override
    public int getOrderTotalByPhone(String phone) {
        String hql = "select count(*) from Order O where O.phone = ? and O.isDel = 0";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, phone);
        List list = query.list();
        if (!CollectionsUtils.isNull(list) && null != list.get(0)) {
            return StringUtils.StringToInt(list.get(0).toString());
        }
        return 0;
    }



    @Override
    public Object getObjectById(Class cls, long id) {
        String hql = "from Order O where O.orderId = ?  and O.isDel = 0 ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, id);
        List<Order> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

}

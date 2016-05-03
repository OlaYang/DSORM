package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.CollectGoodsDao;
import com.meiqi.app.pojo.CollectGoods;

@Service
public class CollectGoodsDaoImpl extends BaseDaoImpl implements CollectGoodsDao {

    @Override
    public List<CollectGoods> getAllCollectGoods(Class<CollectGoods> cls, long userId, int firstResult, int maxResults) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("userId", userId)).addOrder(Order.desc("addTime"));
        criteria.setFirstResult(firstResult).setMaxResults(maxResults);
        return criteria.list();
    }

}

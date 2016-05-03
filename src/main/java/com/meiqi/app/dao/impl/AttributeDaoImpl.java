package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.AttributeDao;
import com.meiqi.app.pojo.Attribute;

@Service
public class AttributeDaoImpl extends BaseDaoImpl implements AttributeDao {

    @Override
    public List<Attribute> getFilterAttributeListByCartId(Class<Attribute> cls, long cartId) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("catId", cartId));
        criteria.add(Restrictions.eq("attrInputType", (byte) 1));
        criteria.add(Restrictions.isNotNull("attrValues"));
        criteria.add(Restrictions.eq("isSell", (byte) 0));
        return criteria.list();
    }



    @Override
    public List<Long> getAttributeIdByCartId(Class<Attribute> cls, long cartId) {
        String hql = "select A.attrId from Attribute A where A.catId =? order by A.sortOrder";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, cartId);
        return query.list();
    }
}

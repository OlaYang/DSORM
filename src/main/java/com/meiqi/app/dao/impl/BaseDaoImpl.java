package com.meiqi.app.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.meiqi.app.common.utils.MySessionFactory;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.BaseDao;

@Repository
public class BaseDaoImpl extends MySessionFactory implements BaseDao {

    @Override
    public long addObejct(Object object) {
        Serializable pKey = getSession().save(object);
        getSession().flush();
        return ((Long) pKey).longValue();
    }



    @Override
    public void updateObejct(Object object) {
        getSession().update(object);
        getSession().flush();
    }



    @Override
    public void saveOrUpdateObejct(Object object) {

        getSession().saveOrUpdate(object);
        getSession().flush();
    }



    @Override
    public void deleteObejct(Object object) {
        getSession().delete(object);
        getSession().flush();
    }



    @Override
    public Object getObjectById(Class cls, long id) {

        return getSession().get(cls, id);
    }



    @Override
    public List<Object> getAllObject(Class cls, int firstResult, int maxResults) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.setFirstResult(firstResult).setMaxResults(maxResults);
        return criteria.list();
    }



    @Override
    public List<Object> getObjectByProperty(Class cls, String[] propertyName, Object[] propertyValue, int firstResult,
            int maxResults) {
        if (null == propertyName || propertyName.length < 1) {
            return null;
        }
        String className = cls.getName();
        String headerChar = StringUtils.getFirstTextHeadChar(className);
        StringBuffer hqlSB = new StringBuffer();
        hqlSB.append("from " + className + " " + headerChar + " where ");
        for (int i = 0; i < propertyName.length; i++) {
            hqlSB.append(headerChar + "." + propertyName[i] + "=" + propertyValue[i]);
            if (i != propertyName.length - 1) {
                hqlSB.append(" and ");
            }
        }
        Query query = getSession().createQuery(hqlSB.toString());
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }



    @Override
    public List<Object> getAllObject(Class cls) {
        Criteria criteria = getSession().createCriteria(cls);
        return criteria.list();
    }



    @Override
    public void clearSiteCache() {
        getSession().clear();
    }



    @Override
    public void flush() {
        getSession().flush();

    }

}

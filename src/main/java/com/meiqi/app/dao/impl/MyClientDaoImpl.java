package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.MyClientDao;
import com.meiqi.app.pojo.MyClient;

@Service
public class MyClientDaoImpl extends BaseDaoImpl implements MyClientDao {

    @Override
    public List<MyClient> getMyClientAddress(Class<MyClient> cls, long designerId) {
        String hql = "from MyClient MC where MC.designerId = ? order by MC.addTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, designerId);
        return query.list();
    }



    @Override
    public MyClient getMyClientByProperty(Class<MyClient> cls, long designerId, String phone) {
        String hql = "from MyClient MC where MC.designerId = ? and MC.consignee.phone = ? order by MC.addTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, designerId);
        query.setParameter(1, phone);
        List list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return (MyClient) list.get(0);
        }
        return null;
    }



    @Override
    public MyClient getMyClientAddress(Class<MyClient> cls, long designerId, long consigneeId) {
        String hql = "from MyClient MC where MC.designerId = ? and MC.consignee.consigneeId = ? order by MC.addTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, designerId);
        query.setParameter(1, consigneeId);
        List list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return (MyClient) list.get(0);
        }
        return null;
    }

}

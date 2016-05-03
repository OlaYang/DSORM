package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.MyBankDao;
import com.meiqi.app.pojo.MyBank;

@Service
public class MyBankDaoImpl extends BaseDaoImpl implements MyBankDao {

    @Override
    public MyBank getMyBank(Class<MyBank> cls, long userId) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("userId", userId));
        List<MyBank> list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }

        return null;
    }



    @Override
    public MyBank getMyBank(Class<MyBank> cls, long userId, long myBankId) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("userId", userId)).add(Restrictions.eq("myBankId", myBankId));
        List<MyBank> list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

}

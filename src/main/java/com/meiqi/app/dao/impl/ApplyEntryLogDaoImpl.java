package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.ApplyEntryLogDao;
import com.meiqi.app.pojo.ApplyEntryLog;

@Service
public class ApplyEntryLogDaoImpl extends BaseDaoImpl implements ApplyEntryLogDao {

    @Override
    public ApplyEntryLog getApplyEntryLog(long userId) {
        String hql = "from ApplyEntryLog AEL where AEL.userId = ? order by AEL.applyTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        List<ApplyEntryLog> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

}

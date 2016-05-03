package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.dao.InviteCodeDao;
import com.meiqi.app.pojo.InviteCode;

@Service
public class InviteCodeDaoImpl extends BaseDaoImpl implements InviteCodeDao {

    @Override
    public List<InviteCode> getTodaySendNumber(long userId) {
        String hql = "from InviteCode IC where IC.sendUserId =? and IC.sendTime>=? and IC.sendTime<=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId).setParameter(1, DateUtils.getTodayStartSecond())
                .setParameter(2, DateUtils.getSecond());
        return query.list();
    }



    @Override
    public List<InviteCode> getTodaySendNumber(String receivePhone) {
        String hql = "from InviteCode IC where IC.receivePhone =? and IC.sendTime>=? and IC.sendTime<=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, receivePhone).setParameter(1, DateUtils.getTodayStartSecond())
                .setParameter(2, DateUtils.getSecond());
        return query.list();
    }



    /**
     * 
     * @Title: getInvideCOdeByCode
     * @Description:查询邀约码是否已存在
     * @param @param cls
     * @param @param code
     * @param @return
     * @throws
     */
    @Override
    public InviteCode getInvideCodeByCode(Class<InviteCode> cls, String code, Byte status) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("code", code));
        if (null != status) {
            criteria.add(Restrictions.eq("status", status));
        }
        List<InviteCode> inviteCodeList = criteria.list();
        if (!CollectionsUtils.isNull(inviteCodeList)) {
            return inviteCodeList.get(0);
        }
        return null;
    }



    @Override
    public List<InviteCode> getInvideCodeByUserId(long userId, int firstResult, int maxResults) {
        String hql = "from InviteCode IC where IC.sendUserId =? order by IC.sendTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }



    @Override
    public int getInviteCodeTotal(long userId) {
        String hql = "from InviteCode IC where IC.sendUserId =?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        List list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.size();
        }
        return 0;
    }

}

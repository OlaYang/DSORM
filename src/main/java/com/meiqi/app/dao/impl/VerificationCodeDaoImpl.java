package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.dao.VerificationCodeDao;
import com.meiqi.app.pojo.VerificationCode;

@Service
public class VerificationCodeDaoImpl extends BaseDaoImpl implements VerificationCodeDao {

    @Override
    public VerificationCode getCode(Class<VerificationCode> cls, String objectId, byte type) {
        String hql = "from VerificationCode VC where VC.isValid = 1 and VC.objectId = ? and VC.validTime > ? and VC.type= ? order by VC.validTime desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, objectId).setParameter(1, DateUtils.getSecond()).setParameter(2, type);
        List<VerificationCode> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public int invalidCode(String phone, byte type) {
        String hql = "update VerificationCode VC set VC.isValid=0 where VC.objectId= ? and VC.isValid=1 and VC.type = ?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, phone);
        query.setParameter(1, type);
        return query.executeUpdate();
    }

}

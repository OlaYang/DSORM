package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.UsersDao;
import com.meiqi.app.pojo.Users;

@Service
public class UsersDaoImpl extends BaseDaoImpl implements UsersDao {

    // @Override
    // public Users getUsersByPhone(Class<Users> cls, String phone) {
    // String hql =
    // "from Users U where U.phone = ? and U.isValidated = 1 and U.roleId > 1";
    // Query query = getSession().createQuery(hql);
    // query.setParameter(0, phone);
    // List<Users> list = query.list();
    // if (!CollectionsUtils.isNull(list)) {
    // return list.get(0);
    // }
    // return null;
    // }

    @Override
    public Users getUsersByUserName(Class<Users> cls, String userName) {
        String hql = "from Users U where U.userName = ? and U.isValidated = 1 and U.roleId > 1";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userName);
        List<Users> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Users getAllTypeUsersByUserName(Class<Users> cls, String userName) {
        String hql = "from Users U where U.userName = ? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userName);
        List<Users> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Users getUsersByDeviceId(Class<Users> cls, String deviceId) {
        String hql = "from Users U where U.deviceId = ?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, deviceId);
        List<Users> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Users getObjectById(long id) {
        String hql = "from Users U where U.userId = ? and U.isValidated = 1 and U.roleId > 1";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, id);
        List<Users> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Users getAllTypeUserById(long id) {
        String hql = "from Users U where U.userId = ?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, id);
        List<Users> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public void deleteAbandonUser(String userName, long userId) {
        String hql = "delete Users U where U.userId != ? and  U.isValidated != 1 and (U.userName = ? or U.phone= ? )";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        query.setParameter(1, userName);
        query.setParameter(2, userName);
        query.executeUpdate();
        getSession().flush();
    }

}

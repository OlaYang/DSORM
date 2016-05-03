package com.meiqi.app.dao;

import com.meiqi.app.pojo.Users;

public interface UsersDao extends BaseDao {
    Users getObjectById(long id);



    Users getUsersByUserName(Class<Users> cls, String userName);



    Users getAllTypeUserById(long id);



    Users getAllTypeUsersByUserName(Class<Users> cls, String userName);



    Users getUsersByDeviceId(Class<Users> cls, String deviceId);



    void deleteAbandonUser(String userName, long userId);
}

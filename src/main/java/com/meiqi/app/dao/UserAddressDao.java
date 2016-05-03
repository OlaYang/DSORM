package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.UserAddress;

public interface UserAddressDao extends BaseDao {
    List<UserAddress> getMyClientAddress(Class<UserAddress> cls, List<Long> addressIdList);
}

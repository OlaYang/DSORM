package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.MyClient;

public interface MyClientDao extends BaseDao {
    List<MyClient> getMyClientAddress(Class<MyClient> cls, long designerId);



    MyClient getMyClientAddress(Class<MyClient> cls, long designerId, long consigneeId);



    MyClient getMyClientByProperty(Class<MyClient> cls, long designerId, String phone);
}

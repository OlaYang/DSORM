package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.MyClient;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.pojo.UserAddress;
import com.meiqi.app.pojo.Users;

public interface MyClientService {
    List<Users> getMyClientList(long designerId);



    List<UserAddress> getMyClientAddress(long designerId);



    MyClient getMyClientByProperty(long designerId, String phone);



    UserAddress getUserAddressByConsigneeId(long designerId, long consigneeId);



    boolean addMyClientAddress(MyClient myClient);



    boolean updateMyClientAddress(UserAddress userAddress);



    boolean deleteMyClientAddress(UserAddress userAddress, long designerId);



    Region getRegionBySelRegionId(long regionId);

}

package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.MyClientDao;
import com.meiqi.app.dao.RegionDao;
import com.meiqi.app.dao.UserAddressDao;
import com.meiqi.app.pojo.MyClient;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.pojo.UserAddress;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.service.MyClientService;

/**
 * 
 * @ClassName: MyClientServiceImpl
 * @Description:
 * @author sky2.0
 * @date 2015年4月26日 下午4:13:54
 *
 */
@Service
public class MyClientServiceImpl implements MyClientService {
    private static final Logger LOG = Logger.getLogger(MyClientServiceImpl.class);
    Class<MyClient>             cls = MyClient.class;
    @Autowired
    private MyClientDao         myClientDao;
    @Autowired
    private UserAddressDao      userAddressDao;
    @Autowired
    private RegionDao           regionDao;

    @Override
    public List<Users> getMyClientList(long designerId) {
        return null;
    }



    /**
     * 
     * @Title: getMyClientAddress
     * @Description:获取所用用户的address
     * @param @param designerId
     * @param @return
     * @throws
     */
    @Override
    public List<UserAddress> getMyClientAddress(long designerId) {
        LOG.info("Function:getMyClientAddress.Start.");

        List<UserAddress> addressList = null;
        List<MyClient> myClientList = myClientDao.getMyClientAddress(cls, designerId);
        if (!CollectionsUtils.isNull(myClientList)) {
            addressList = new ArrayList<UserAddress>();
            for (MyClient myClient : myClientList) {
                UserAddress userAddress = myClient.getConsignee();
                // 拼装linked region
                Region region = regionDao.getRegionByRegionId(userAddress.getRegionId());
                region = regionDao.getLinkedRegionByRegion(region);
                userAddress.setRegion(region);
                addressList.add(userAddress);
            }
        }
        LOG.info("Function:getMyClientAddress.End.");
        return addressList;
    }



    /**
     * 
     * @Title: addMyClientAddress
     * @Description:新增收货地址
     * @param @param myClient
     * @param @return
     * @throws
     */
    @Override
    public boolean addMyClientAddress(MyClient myClient) {
        LOG.info("Function:addMyClientAddress.Start.");
        boolean result = false;
        assembleUsesAddress(myClient.getConsignee());
        myClientDao.addObejct(myClient);
        result = true;
        LOG.info("Function:addMyClientAddress.End.");
        return result;
    }



    /**
     * 
     * @Title: assembleUsesAddress
     * @Description:拼装 usersaddress
     * @param @param userAddress
     * @return void
     * @throws
     */
    private void assembleUsesAddress(UserAddress userAddress) {
        Region region = userAddress.getRegion();
        if (null != region) {
            // 获取父region
            region = regionDao.getLinkedRegionByRegion(region);
            userAddress.assembleUsesAddress(userAddress, region);
        }

    }



    /**
     * 
     * @Title: getMyClientByProperty
     * @Description:根据参数 获取 myclient
     * @param @param myClient
     * @param @return
     * @throws
     */
    @Override
    public MyClient getMyClientByProperty(long designerId, String phone) {
        LOG.info("Function:getMyClientByProperty.Start.");
        MyClient oldMyClient = null;
        oldMyClient = myClientDao.getMyClientByProperty(cls, designerId, phone);
        LOG.info("Function:getMyClientByProperty.End.");
        return oldMyClient;
    }



    /**
     * 
     * @Title: updateMyClientAddress
     * @Description:修改收货地址
     * @param @param myClient
     * @param @return
     * @throws
     */
    @Override
    public boolean updateMyClientAddress(UserAddress userAddress) {
        LOG.info("Function:updateMyClientAddress.Start.");
        boolean result = false;
        assembleUsesAddress(userAddress);
        userAddressDao.updateObejct(userAddress);
        result = true;
        LOG.info("Function:updateMyClientAddress.End.");
        return result;
    }



    /**
     * 
     * @Title: getUserAddressConsigneeId
     * @Description:根据地址id 获取地址
     * @param @param consigneeId
     * @param @return
     * @throws
     */
    @Override
    public UserAddress getUserAddressByConsigneeId(long designerId, long consigneeId) {
        LOG.info("Function:getUserAddressConsigneeId.Start.");
        UserAddress userAddress = null;
        MyClient myClient = (MyClient) myClientDao.getMyClientAddress(cls, designerId, consigneeId);
        if (null != myClient) {
            userAddress = myClient.getConsignee();
        }
        LOG.info("Function:getUserAddressConsigneeId.End.");
        return userAddress;
    }



    /**
     * 
     * @Title: deleteMyClientAddress
     * @Description:删除address
     * @param @param userAddress
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteMyClientAddress(UserAddress userAddress, long designerId) {
        LOG.info("Function:deleteMyClientAddress.Start.");
        boolean result = false;

        MyClient myClient = getMyClientByProperty(designerId, userAddress.getPhone());
        if (null != myClient) {
            myClientDao.deleteObejct(myClient);
            // userAddressDao.deleteObejct(userAddress);
            result = true;
        }

        LOG.info("Function:deleteMyClientAddress.End.");
        return result;
    }



    /**
     * 
     * @Title: getRegionBySelRegionId
     * @Description:通过选择的region id 来获取region 该region type应该为 3 or
     *                          4,否者选择了无效region
     * @param @param regionId
     * @param @return
     * @throws
     */
    @Override
    public Region getRegionBySelRegionId(long regionId) {
        LOG.info("Function:getRegionBySelRegionId.Start.");
        Region region = regionDao.getRegionBySelRegionId(regionId);
        LOG.info("Function:getRegionBySelRegionId.End.");
        return region;
    }

}

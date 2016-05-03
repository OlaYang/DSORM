package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.UserAddressDao;
import com.meiqi.app.pojo.UserAddress;
@Service
public class UserAddressDaoImpl extends BaseDaoImpl implements UserAddressDao {

    @Override
    public List<UserAddress> getMyClientAddress(Class<UserAddress> cls, List<Long> addressIdList) {
        String hql = "select new UserAddress(UA.consigneeId,UA.name, UA.provinceId, UA.cityId, UA.districtId, UA.extendId,UA.detail,UA.phone) "
                + " from UserAddress UA where UA.consigneeId in (:addressIdList) ";
        Query query = getSession().createQuery(hql);
        query.setParameterList("addressIdList", addressIdList);
        return query.list();
    }

}

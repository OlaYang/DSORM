package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.BrandDao;
import com.meiqi.app.pojo.Brand;

@Service
public class BrandDaoImpl extends BaseDaoImpl implements BrandDao {

    @Override
    public List<Brand> getBrand(Class<Brand> cls, Long[] brandIdArray) {
        String hql = "from Brand b where b.brandId in (:brandIdArray) order by b.sortOrder";
        Query query = getSession().createQuery(hql);
        query.setParameterList("brandIdArray", brandIdArray);
        return query.list();
    }

}

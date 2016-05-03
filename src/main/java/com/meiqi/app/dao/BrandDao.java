package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Brand;

public interface BrandDao extends BaseDao {
    List<Brand> getBrand(Class<Brand> cls, Long[] brandIdArray);
}

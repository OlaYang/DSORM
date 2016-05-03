package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Attribute;

public interface AttributeDao extends BaseDao {
    List<Attribute> getFilterAttributeListByCartId(Class<Attribute> cls, long cartId);



    List<Long> getAttributeIdByCartId(Class<Attribute> cls, long cartId);
}

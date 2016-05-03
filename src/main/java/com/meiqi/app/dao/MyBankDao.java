package com.meiqi.app.dao;

import com.meiqi.app.pojo.MyBank;

public interface MyBankDao extends BaseDao {

    MyBank getMyBank(Class<MyBank> cls, long userId);

    MyBank getMyBank(Class<MyBank> cls, long userId, long myBankId);

}

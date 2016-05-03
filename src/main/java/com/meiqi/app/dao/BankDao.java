package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Bank;

public interface BankDao extends BaseDao {

    List<Bank> getAllBank(Class<Bank> cls);
}

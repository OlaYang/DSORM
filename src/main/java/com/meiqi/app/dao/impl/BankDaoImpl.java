package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.BankDao;
import com.meiqi.app.pojo.Bank;

@Service
public class BankDaoImpl extends BaseDaoImpl implements BankDao {

    @Override
    public List<Bank> getAllBank(Class<Bank> cls) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.addOrder(Order.asc("sortOrder"));
        return criteria.list();
    }

}

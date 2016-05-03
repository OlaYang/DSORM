package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Bank;
import com.meiqi.app.pojo.MyBank;

public interface BankService {
    List<Bank> getAllBank();



    MyBank getMyBank(long userId);



    MyBank getMyBank(long userId, long myBankId);



    long addMyBank(MyBank myBank);



    boolean updateMyBank(MyBank myBank);



    boolean deleteMyBank(MyBank myBank);
}

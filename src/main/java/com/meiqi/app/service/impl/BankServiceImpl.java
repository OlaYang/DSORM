package com.meiqi.app.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.dao.BankDao;
import com.meiqi.app.dao.MyBankDao;
import com.meiqi.app.pojo.Bank;
import com.meiqi.app.pojo.MyBank;
import com.meiqi.app.service.BankService;

/**
 * 
 * @ClassName: BankServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年5月27日 下午6:26:37
 *
 */
@Service
public class BankServiceImpl implements BankService {
    private static final Logger LOG = Logger.getLogger(BankServiceImpl.class);
    Class<Bank>                 cls = Bank.class;
    @Autowired
    private BankDao             bankDao; 
    @Autowired
    private MyBankDao           myBankDao;



    /**
     * 
     * @Title: getAllBank
     * @Description:2.4.6.5 获取支持的银行信息
     * @param @return
     * @throws
     */
    @Override
    public List<Bank> getAllBank() {
        LOG.info("Function:getAllBank.Start.");
        List<Bank> bankList = bankDao.getAllBank(cls);
        LOG.info("Function:getAllBank.End.");
        return bankList;
    }



    /**
     * 
     * @Title: getMyBank
     * @Description:获取用户绑定的银行卡
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public MyBank getMyBank(long userId) {
        LOG.info("Function:getMyBank.Start.");

        MyBank myBank = myBankDao.getMyBank(MyBank.class, userId);
        if (null != myBank) {
            Bank bank = (Bank) bankDao.getObjectById(cls, myBank.getBankId());
            myBank.setBank(bank);
        }
        LOG.info("Function:getMyBank.End.");
        return myBank;
    }



    /**
     * 
     * @Title: addMyBank
     * @Description:添加银行卡
     * @param @param myBank
     * @param @return
     * @throws
     */
    @Override
    public long addMyBank(MyBank myBank) {
        LOG.info("Function:addMyBank.Start.");
        // 添加时间
        myBank.setAddTime(DateUtils.getSecond());
        long myBankId = myBankDao.addObejct(myBank);
        LOG.info("Function:addMyBank.End.");
        return myBankId;
    }



    /**
     * 
     * @Title: updateMyBank
     * @Description:更换银行卡
     * @param @param myBank
     * @param @return
     * @throws
     */
    @Override
    public boolean updateMyBank(MyBank myBank) {
        LOG.info("Function:updateMyBank.Start.");
        boolean result = false;
        myBankDao.updateObejct(myBank);
        result = true;
        LOG.info("Function:updateMyBank.End.");
        return result;
    }



    /**
     * 
     * @Title: getMyBank
     * @Description:获取用户绑定的银行卡
     * @param @param userId
     * @param @param myBankId
     * @param @return
     * @throws
     */
    @Override
    public MyBank getMyBank(long userId, long myBankId) {
        LOG.info("Function:getMyBank.Start.");
        MyBank myBank = myBankDao.getMyBank(MyBank.class, userId, myBankId);
        LOG.info("Function:getMyBank.End.");
        return myBank;
    }



    /**
     * 
     * @Title: deleteMyBank
     * @Description:解除银行卡绑定
     * @param @param myBank
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteMyBank(MyBank myBank) {
        LOG.info("Function:deleteMyBank.Start.");
        boolean result = false;
        myBankDao.deleteObejct(myBank);
        LOG.info("Function:deleteMyBank.End.");
        result = true;
        return result;
    }

}

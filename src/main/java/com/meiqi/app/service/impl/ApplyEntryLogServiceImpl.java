package com.meiqi.app.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.ApplyEntryLogDao;
import com.meiqi.app.pojo.ApplyEntryLog;
import com.meiqi.app.service.ApplyEntryLogService;

/**
 * 
 * @ClassName: ApplyEntryLogServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年6月17日 下午8:01:29 
 *
 */
@Service
public class ApplyEntryLogServiceImpl implements ApplyEntryLogService {
    private static final Logger LOG = Logger.getLogger(ApplyEntryLogServiceImpl.class);
    Class<ApplyEntryLog>        cls = ApplyEntryLog.class;
    private ApplyEntryLogDao    applyEntryLogDao;



    public ApplyEntryLogDao getApplyEntryLogDao() {
        return applyEntryLogDao;
    }



    public void setApplyEntryLogDao(ApplyEntryLogDao applyEntryLogDao) {
        this.applyEntryLogDao = applyEntryLogDao;
    }



    /**
     * 
     * @Title: addEApplyEntryLog
     * @Description:添加一条申请入驻记录
     * @param @param applyEntryLog
     * @throws
     */
    @Override
    public void addEApplyEntryLog(ApplyEntryLog applyEntryLog) {
        LOG.info("Function:addEApplyEntryLog.Start.");

        LOG.info("Function:addEApplyEntryLog.End.");
    }

}

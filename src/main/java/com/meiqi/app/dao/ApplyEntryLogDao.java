package com.meiqi.app.dao;

import com.meiqi.app.pojo.ApplyEntryLog;

public interface ApplyEntryLogDao extends BaseDao {

    ApplyEntryLog getApplyEntryLog(long userId);

}

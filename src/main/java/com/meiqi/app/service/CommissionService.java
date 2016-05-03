package com.meiqi.app.service;

import com.meiqi.app.pojo.Commission;

public interface CommissionService {

    String getAllCommission(long userId, Commission info, String platString);

    String getCommissionDetail(long userId, long commissionId, String platString);

}

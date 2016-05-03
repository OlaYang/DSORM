package com.meiqi.app.dao;

import com.meiqi.app.pojo.VerificationCode;

public interface VerificationCodeDao extends BaseDao {
    VerificationCode getCode(Class<VerificationCode> cls, String objectId, byte type);



    int invalidCode(String phone, byte type);
}

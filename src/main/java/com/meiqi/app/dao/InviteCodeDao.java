package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.InviteCode;

public interface InviteCodeDao extends BaseDao {

    List<InviteCode> getTodaySendNumber(long userId);



    List<InviteCode> getTodaySendNumber(String receivePhone);



    InviteCode getInvideCodeByCode(Class<InviteCode> cls, String code, Byte status);



    List<InviteCode> getInvideCodeByUserId(long userId, int firstResult, int maxResults);



    int getInviteCodeTotal(long userId);

}

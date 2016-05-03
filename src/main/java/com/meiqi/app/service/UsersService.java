package com.meiqi.app.service;

import java.util.List;
import java.util.Map;

import com.meiqi.app.pojo.ApplyEntryLog;
import com.meiqi.app.pojo.InviteCode;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.VerificationCode;

public interface UsersService {

    Users getUsers(long userId);



    Users loginUsers(String phone, String password, String lastIp);



    Users registerUsers(Users users);



    Users addAnonymousUser(Users users);



    boolean hasUserName(String userName);



    boolean hasAllTypeUsersByUserName(String userName);



    boolean isValidCode(String phone, String code, byte type, boolean setInvalid);



    boolean updatePhone(long userId, String phone);



    Users isValidCode(Users user);



    //String addCode(String phone, byte type, Map<String, Object> param);



    boolean updateDesignerInfo(Users designer);



    Users isValidPassword(Users user);



//    boolean updatePassword(Users user);



    Users getUsers(String deviceId);



    ApplyEntryLog getApplyEntryLog(long userId);



//    boolean checkSendEnabled(long userId, String receivePhone);



//    void sendInviteCode(long userId, String receivePhone) throws Exception;
    
//    void sendInviteCode(long userId, String receivePhone, String templateName) throws Exception;



    List<InviteCode> getInviteCodeList(long userId, int pageIndex, int pageSize);



//    InviteCode checkInviteCode(String inviteCode, Byte status);



    String getUserCenter(long userId, int userRole, String platString);



    ApplyEntryLog getApplyEntryStatus(long userId, String plat);



    String getInviteCodeTotal(long userId);



    /**
     * 
     * 删除入驻申请失败的用户，根据本次入驻申请用户的手机号
     *
     * @param userName
     * @param userId
     */
    public void deleteAbandonUser(String userName, long userId);



    String isValidVerificationCode(String phone, byte type);



    boolean updatePassword(Users oldUser);



    void setCompany(Users user);



    boolean checkInviteCode(String code);



    InviteCode getInviteCode(String code);


    Users getUserByUserName(String userName);

    
    Users getUserByUserId(long userId);



    int getUserFrom(int platInt);
    

}

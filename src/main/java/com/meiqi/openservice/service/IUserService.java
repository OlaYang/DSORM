package com.meiqi.openservice.service;

import com.meiqi.openservice.bean.user.User;

public interface IUserService {

    public User getUserByUserId(long userId);



    public String getBindEmailCodeType(byte bindEmailStep, int web_site);

}
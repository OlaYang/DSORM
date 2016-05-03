package com.meiqi.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.pojo.Users;
import com.meiqi.app.service.InviteCodeService;
import com.meiqi.app.service.UsersService;

@Service
public class InviteCodeServiceImpl implements InviteCodeService {
    @Autowired
    private UsersService usersService;



    @Override
    public String getInviteCode(long userId) {
        Users user = usersService.getUserByUserId(userId);
        if (null != user) {
            return user.getInviteCode();
        }
        return null;
    }

}

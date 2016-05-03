package com.meiqi.openservice.action.login.thirdLogin.impl;

import org.apache.log4j.Logger;

import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import com.meiqi.openservice.bean.LoginResponseInfo;

public class LoginWithSinaActionImpl {
    private String code;
    
    public LoginWithSinaActionImpl(String code)
    {
        this.code = code;
    }
    
    public LoginResponseInfo login()
    {
     // 2. 通过code去获取access_token
        Oauth oauth = new Oauth();
        AccessToken accessToken;

        // 登录之后的用户信息
        LoginResponseInfo loginResponseInfo = new LoginResponseInfo();
        
        try {
            accessToken = oauth.getAccessTokenByCode(code);

        
            // 3.通过access_token获取用户uid，并通过uid获取用户信息
            Account account = new Account(accessToken.getAccessToken());
            
            JSONObject uidJson = account.getUid();
            
            Users oAuthUserInfoGetter = new Users(accessToken.getAccessToken());
            
            // 获取到的认证用户信息
            User oAuthUser = oAuthUserInfoGetter.showUserById((String) uidJson.get("uid"));
            
            // 获取到用户信息之后，需要转成lejj所需要的用户信息,其中不支持email和realName
            loginResponseInfo.setEmail("");
            loginResponseInfo.setRealName("");
            loginResponseInfo.setSmall_avatar(oAuthUser.getProfileImageUrl());
            loginResponseInfo.setUid(oAuthUser.getId());
            loginResponseInfo.setUserName(oAuthUser.getName());
            
            loginResponseInfo.setCode("0");
            loginResponseInfo.setDescription("success");
        
        } catch (WeiboException e) {
            Logger.getLogger("monitor").warn("login from sina fail", e);
            loginResponseInfo.setCode("-1");
            loginResponseInfo.setDescription("fail");
        } catch (JSONException e) {
            Logger.getLogger("monitor").warn("parse uidJson Object fail", e);
            loginResponseInfo.setCode("-1");
            loginResponseInfo.setDescription("fail");
        } catch (Exception e) {
            Logger.getLogger("monitor").warn("login from sina weibo", e);
            loginResponseInfo.setCode("-1");
            loginResponseInfo.setDescription("fail");  
        }
        return loginResponseInfo;
    }
}

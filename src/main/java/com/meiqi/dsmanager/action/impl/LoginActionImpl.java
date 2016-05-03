package com.meiqi.dsmanager.action.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.ILoginAction;
import com.meiqi.dsmanager.po.rule.login.LoginReqInfo;
import com.meiqi.dsmanager.po.rule.login.LoginRespInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.commons.util.RuleExceptionUtil;

@Service
public class LoginActionImpl implements ILoginAction {
    @Autowired
    private RuleExceptionUtil ruleExceptionUtil;

    @Override
    public LoginRespInfo login(LoginReqInfo reqInfo) {
        long start = System.currentTimeMillis();
        boolean flag = false;// 登录状态标记：true->成功,false->失败
        LoginRespInfo respInfo = new LoginRespInfo();
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_name", reqInfo.getUserName());
            final TService po = Services.getService(Services.SERVICE_USER_INFO);
            D2Data data = DataUtil.getD2Data(po, param);
			
            Object password = data.getValue("密码", 0);
            Object groupID = data.getValue("组ID", 0);

            if (password == null) {
                throw new IllegalArgumentException("用户不存在, " + reqInfo.getUserName());
            }

            String encryptedPassword = DataUtil.getEncryptedPassword(reqInfo.getPassword());

            if (!encryptedPassword.equalsIgnoreCase(password.toString())) {
                throw new IllegalArgumentException("密码错误");
            }

            respInfo.setGroupID(String.valueOf(groupID));
            
            long end = System.currentTimeMillis();
            Logger.getLogger("monitor").info(Services.SERVICE_USER_INFO + "|" + (end - start));
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
            ruleExceptionUtil.run(e);
        }
        if (!flag) {
            respInfo.setCode("-1");
            respInfo.setDescription("Failed");
        }
        return respInfo;
    }

}

package com.meiqi.app.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Bank;
import com.meiqi.app.pojo.MyBank;
import com.meiqi.app.pojo.ResponseData;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.BankService;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.UsersService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: BankController
 * @Description:
 * @author 杨永川
 * @date 2015年5月27日 下午6:24:33
 *
 */
@Service
public class BankAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(BankAction.class);
    @Autowired
    private BankService         bankService;
    @Autowired
    private UsersService        usersService;
    @Autowired
    private EtagService         eTagService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String plat = getPlatString(request);
        // 获取用户绑定的银行卡
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        if (url.contains("allBank")) {
            // 2.4.6.5 获取支持的银行信息
            String data = getAllBank();
            String key = "allBank";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        } else if (StringUtils.matchByRegex(url, "^bank\\/\\d+$")) {
            long mybankId = StringUtils.StringToLong(url.replaceAll("bank/", ""));
            if ("patch".equals(method)) {
                // 添加银行卡
                MyBank myBank = DataUtil.parse(appRepInfo.getParam(), MyBank.class);
                eTagService.putEtagMarking(request, "bank/" + userId, Long.toString(System.currentTimeMillis()));
                return updateMyBank(mybankId, userId, myBank);
            } else if ("delete".equals(method)) {
                return deleteMyBank(mybankId, userId);
            }

        } else if ("bank".equals(url)) {
            if ("get".equals(method)) {
                String data = getMyBank(userId, plat);
                String key = "bank/" + userId;
                boolean result = eTagService.toUpdatEtag1(request, response, key, data);
                if (result) {
                    return null;
                }
                return data;
            } else if ("put".equals(method)) {
                // 添加银行卡
                MyBank myBank = DataUtil.parse(appRepInfo.getParam(), MyBank.class);
                eTagService.putEtagMarking(request, "bank/" + userId, Long.toString(System.currentTimeMillis()));
                return addMyBank(myBank, userId);
            }
        }
        return null;
    }



    /**
     * 
     * @Title: getAllBank
     * @Description:2.4.6.5 获取支持的银行信息
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getAllBank() {
        LOG.info("Function:getAllBank.Start.");
        List<Bank> bankList = bankService.getAllBank();
        String allBankJson = JsonUtils.listFormatToString(bankList);
        LOG.info("Function:getAllBank.End.");
        return allBankJson;
    }



    /**
     * 
     * @Title: getAllBank
     * @Description:获取用户绑定的银行卡
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getMyBank(long userId, String plat) {
        LOG.info("Function:getMyBank.Start.");
        MyBank myBanks = bankService.getMyBank(userId);
        String myBankJson = null;
        if (ContentUtils.PLAT_IPAD.equals(plat)) {
            ResponseData repMessage = new ResponseData(200, "success", null, myBanks);
            myBankJson = JsonUtils.objectFormatToString(repMessage);
        } else {
            myBankJson = JsonUtils.objectFormatToString(myBanks);
        }
        LOG.info("Function:getMyBank.End.");
        return myBankJson;
    }



    /**
     * 
     * @Title: addMyBank
     * @Description:用户绑定银行卡
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addMyBank(MyBank myBank, long userId) {
        LOG.info("Function:addMyBank.Start.");
        String addMyBankJson = "";
        Users user = usersService.getUsers(userId);
        if (null == user) {
            return JsonUtils.getAuthorizationErrorJson();
        }
        MyBank savedMyBank = bankService.getMyBank(userId);
        if (null != savedMyBank) {
            return JsonUtils.getErrorJson("只能绑定一张银行卡!", null);
        }
        addMyBankJson = verifyMyBank(myBank);
        if (!StringUtils.isBlank(addMyBankJson)) {
            return addMyBankJson;
        }
        boolean validCode = usersService.isValidCode(user.getPhone(), myBank.getCode(), (byte) 3, true,myBank.isMakeInvalid());
        if (!validCode) {
            return JsonUtils.getErrorJson("验证码无效!", null);
        }

        myBank.setUserId(userId);
        long myBankId = bankService.addMyBank(myBank);
        if (0 != myBankId) {
            myBank.setMyBankId(myBankId);
            addMyBankJson = JsonUtils.objectFormatToString(myBank);
        } else {
            addMyBankJson = JsonUtils.getErrorJson("添加失败,请重试!", null);
        }
        LOG.info("Function:addMyBank.End.");
        return addMyBankJson;
    }



    /**
     * 
     * @Title: updateMyBank
     * @Description:更换绑定银行卡
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String updateMyBank(long myBankId, long userId, MyBank myBank) {
        LOG.info("Function:updateMyBank.Start.");
        String updateMyBankJson = "";

        MyBank savedMyBank = bankService.getMyBank(userId, myBankId);
        if (null == savedMyBank) {
            return JsonUtils.getErrorJson("该银行卡没有绑定!", null);
        }

        Users user = usersService.getUsers(userId);
        // 验证输入信息
        updateMyBankJson = verifyMyBank(myBank);
        if (!StringUtils.isBlank(updateMyBankJson)) {
            return updateMyBankJson;
        }
        boolean validCode = usersService.isValidCode(user.getPhone(), myBank.getCode(), (byte) 3, true,myBank.isMakeInvalid());
        if (!validCode) {
            return JsonUtils.getErrorJson("验证码无效!", null);
        }

        savedMyBank.setBankId(myBank.getBankId());
        savedMyBank.setCardNumber(myBank.getCardNumber().replaceAll(" ", "")); //清除app上传的银行卡中的空格
        savedMyBank.setUserRealName(myBank.getUserRealName());

        boolean result = bankService.updateMyBank(savedMyBank);
        if (result) {
            updateMyBankJson = JsonUtils.getSuccessJson(null);
        } else {
            updateMyBankJson = JsonUtils.getErrorJson("添加失败,请重试!", null);
        }
        LOG.info("Function:updateMyBank.End.");
        return updateMyBankJson;
    }



    /**
     * 
     * @Title: updateMyBank
     * @Description:解除绑定银行卡
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String deleteMyBank(@PathVariable long myBankId, long userId) {
        LOG.info("Function:deleteMyBank.Start.");
        String delMyBankJson = "";
        MyBank myBank = bankService.getMyBank(userId, myBankId);
        if (null == myBank) {
            return JsonUtils.getErrorJson("该银行卡未绑定!", null);
        }
        boolean result = bankService.deleteMyBank(myBank);
        if (result) {
            delMyBankJson = JsonUtils.getSuccessJson(null);
        } else {
            delMyBankJson = JsonUtils.getErrorJson("解除绑定失败,请重试!", null);
        }
        LOG.info("Function:deleteMyBank.End.");
        return delMyBankJson;
    }



    /**
     * 
     * @Title: verifyMyBank
     * @Description:验证银行卡信息
     * @param @param myBank
     * @param @return
     * @return String
     * @throws
     */
    private String verifyMyBank(MyBank myBank) {
        if (null == myBank) {
            return JsonUtils.getErrorJson("请输入银行卡等信息", null);
        }

        if (StringUtils.isBlank(myBank.getCode())) {
            return JsonUtils.getErrorJson("验证码不能为空!", null);
        }

        if (StringUtils.isBlank(myBank.getCardNumber())) {
            return JsonUtils.getErrorJson("卡号不能为空!", null);
        }
        if (0 == myBank.getBankId()) {
            return JsonUtils.getErrorJson("请选择银行!", null);
        }
        return null;
    }

}

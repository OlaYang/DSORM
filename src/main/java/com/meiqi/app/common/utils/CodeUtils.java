package com.meiqi.app.common.utils;

import java.util.Random;

import com.meiqi.app.common.config.AppSysConfig;

/**
 * 
 * @ClassName: CodeUtils
 * @Description:验证码 邀约码 折扣码生成 工具
 * @author 杨永川
 * @date 2015年6月18日 下午8:51:26
 *
 */
public class CodeUtils {
    // 验证码 模板 0-9数字
    private static final String VERIFICATION_CODE_CHAR_LIST = AppSysConfig.getValue(ContentUtils.VERIFICATION_CODE);
    // 邀约码 模板 26字母+0-9数字
    private static final String INVITE_CODE_CHAR_LIST       = AppSysConfig.getValue(ContentUtils.INVITE_CODE);
    // 折扣码 模板 26字母+0-9数字
    private static final String DISCOUNT_CODE_CHAR_LIST     = AppSysConfig.getValue(ContentUtils.DISCOUNT_CODE);



    /**
     * 
     * @Title: getInviteCode
     * @Description:获取邀约码
     * @param @return
     * @return String
     * @throws
     */
    public static String getInviteCode() {
        if ("true".equals(AppSysConfig.getValue("invite_code_mock"))) {
            return AppSysConfig.getValue("invite_code_mock_value");
        } else {
            // 默认为sysconfig.properties 配置的code
            return getCode(INVITE_CODE_CHAR_LIST, 6);
        }
    }



    /**
     * 
     * @Title: getDiscountCode
     * @Description:获取折扣码
     * @param @return
     * @return String
     * @throws
     */
    public static String getDiscountCode() {
        if ("true".equals(AppSysConfig.getValue("discount_code_mock"))) {
            return AppSysConfig.getValue("discount_code_mock_value");
        } else {
            // 默认为sysconfig.properties 配置的code
            return getCode(DISCOUNT_CODE_CHAR_LIST, 6);
        }
    }

    public static String getBonusCode() {
        if ("true".equals(AppSysConfig.getValue("bonus_code_mock"))) {
            return AppSysConfig.getValue("bonus_code_mock_value");
        } else {
            // 默认为sysconfig.properties 配置的code
            return getCode(DISCOUNT_CODE_CHAR_LIST, 6);
        }
    }


    /**
     * 
     * @Title: getVerificationCode
     * @Description:获取验证码
     * @param @return
     * @return String
     * @throws
     */
    public static String getVerificationCode() {
        if ("true".equals(AppSysConfig.getValue("verification_code_mock"))) {
            return AppSysConfig.getValue("verification_code_mock_value");
        } else {
            return getCode(VERIFICATION_CODE_CHAR_LIST, 4);
        }
    }



    /**
     * 
     * @Title: getCode
     * @Description:获取四位随机
     * @param @return
     * @return String
     * @throws
     */
    public static String getCode() {
        // 默认为sysconfig.properties 配置的code
        return getCode(VERIFICATION_CODE_CHAR_LIST, 4);
    }



    /**
     * 
     * @Title: getCode
     * @Description:获取随机验证码
     * @param @param codeStr
     * @param @param count
     * @param @return
     * @return String
     * @throws
     */
    public static String getCode(String codeStr, int count) {
        String code = "";
        if (StringUtils.isBlank(codeStr)) {
            codeStr = ContentUtils.CODE_STRING;
        }
        String[] codeArray = codeStr.split(ContentUtils.COMMA);
        // 创建Random类的对象rand
        Random rand = new Random();

        for (int i = 0; i < count; ++i) {
            // 在0到str2.length-1生成一个伪随机数赋值给index
            int index = rand.nextInt(codeArray.length - 1);
            // 将对应索引的数组与randStr的变量值相连接
            code += codeArray[index];
        }
        return code;
    }
}

package com.meiqi.openservice.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiqi.app.common.utils.CollectionsUtils;

public class StringUtils {
    
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    
    private static Random randGen = new Random();

    /**
     * 判断字符串是否为Null或trim后长度为0
     * 
     * @param validate
     * @return
     */
    public static boolean isEmpty(String origin) {
        return (origin == null || origin.trim().length() == 0);
    }



    /**
     * @param origin
     * @return 判断字符串非空且trim后长度大于0
     */
    public static boolean isNotEmpty(String origin) {
        return (origin != null && origin.trim().length() > 0);
    }
    
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }
    


    /**
     * 
     * 获取随机字符串
     *
     * @param length
     * @return
     */
    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }
    
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");
        return pattern.matcher(str).matches();
    }



    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?\\d+\\.\\d+$");
        return pattern.matcher(str).matches();
    }



    public static boolean isLetter(String str) {
        if (str == null || str.length() < 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\w\\.-_]*");
        return pattern.matcher(str).matches();
    }



    /**
     * 
     * @Title: isNumeric
     * @Description:判断字符串是数字 数组中有一个不是数字就返回false
     * @param @param strArray
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isNumeric(String[] strArray) {
        if (CollectionsUtils.isNull(strArray)) {
            return false;
        }
        for (String str : strArray) {
            if (!isNumeric(str)) {
                return false;
            }
        }
        return true;
    }



    /**
     * 
     * @Title: StringToInt
     * @Description:string -> int
     * @param @param str
     * @param @return
     * @return int
     * @throws
     */
    public static int StringToInt(String str) {
        if (isEmpty(str) && !isNumeric(str)) {
            return 0;
        }
        Integer integer = Integer.parseInt(str);
        return integer.intValue();
    }



    /**
     * 
     * @Title: StringToShort
     * @Description: string -> short
     * @param @param str
     * @param @return 参数说明
     * @return short 返回类型
     * @throws
     */
    public static short StringToShort(String str) {
        if (isEmpty(str) && !isNumeric(str)) {
            return 0;
        }
        Short value = Short.parseShort(str);
        return value.shortValue();
    }



    /**
     * 
     * @Title: StringToLong
     * @Description:string -> long
     * @param @param str
     * @param @return
     * @return int
     * @throws
     */
    public static long StringToLong(String str) {
        if (isEmpty(str) && !isNumeric(str)) {
            return 0;
        }
        long l = Long.parseLong(str);
        return l;
    }



    /**
     * 
     * @Title: isNumeric
     * @Description:判断字符串是数字
     * @param @param str
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
	
	/**
     * 
     * @Title: StringToDouble
     * @Description:格式化 double字符串,去掉,
     * @param @param str
     * @param @return
     * @return double
     * @throws
     */
    public static double StringToDouble(String str) {
        if (!StringUtils.isEmpty(str)) {
            str = str.replaceAll(ContentUtils.COMMA, "");
            return Double.parseDouble(str);
        }
        return 0;
    }
	
	/**
     * 
     * string -> boolean "true" or "1" -> true
     *
     * @param str
     * @return
     */
    public static boolean String2Boolean(String str) {
        return Boolean.parseBoolean(str) || "1".equals(str);
    }
    
    /**
     * 得到年月日时分+5位随机数用来作为订单编号
     * @return
     */
    public static String getNumber(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String str = df.format(new Date());
        Random rdm = new Random();
        str += rdm.nextInt(9999)+10000;
        return str;
        
    }
}
package com.meiqi.app.common.utils;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringUtils {
    private static final String _BR = "";



    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String[] split(String str, String splitsign) {
        int index;
        if (str == null || splitsign == null) {
            return null;
        }
        ArrayList al = new ArrayList();
        while ((index = str.indexOf(splitsign)) != -1) {
            al.add(str.substring(0, index));
            str = str.substring(index + splitsign.length());
        }
        al.add(str);
        return (String[]) al.toArray(new String[0]);
    }



    public static String replace(String from, String to, String source) {
        if (source == null || from == null || to == null)
            return null;
        StringBuffer str = new StringBuffer("");
        int index = -1;
        while ((index = source.indexOf(from)) != -1) {
            str.append(source.substring(0, index) + to);
            source = source.substring(index + from.length());
            index = source.indexOf(from);
        }
        str.append(source);
        return str.toString();
    }



    public static String htmlencode(String str) {
        if (str == null) {
            return null;
        }
        return replace("\"", "\"", replace("<", "<", str));
    }



    public static String htmldecode(String str) {
        if (str == null) {
            return null;
        }

        return replace("\"", "\"", replace("<", "<", str));
    }



    public static String htmlshow(String str) {
        if (str == null) {
            return null;
        }

        str = replace("<", "<", str);
        str = replace(" ", " ", str);
        str = replace("\r\n", _BR, str);
        str = replace("\n", _BR, str);
        str = replace("\t", "    ", str);
        return str;
    }



    public static String toLength(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length <= 0) {
            return "";
        }
        try {
            if (str.getBytes("GBK").length <= length) {
                return str;
            }
        } catch (Exception e) {
        }
        StringBuffer buff = new StringBuffer();

        int index = 0;
        char c;
        length -= 3;
        while (length > 0) {
            c = str.charAt(index);
            if (c < 128) {
                length--;
            } else {
                length--;
                length--;
            }
            buff.append(c);
            index++;
        }
        buff.append("...");
        return buff.toString();
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
        if (isBlank(str) && !isNumeric(str)) {
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
        if (isBlank(str) && !isNumeric(str)) {
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
        if (isBlank(str) && !isNumeric(str)) {
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
        if (isBlank(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }



    public static String parse(String content) {
        String email = null;
        if (content == null || content.length() < 1) {
            return email;
        }
        // 找出含有@
        int beginPos;
        int i;
        String token = "@";
        String preHalf = "";
        String sufHalf = "";

        beginPos = content.indexOf(token);
        if (beginPos > -1) {
            // 前项扫描
            String s = null;
            i = beginPos;
            while (i > 0) {
                s = content.substring(i - 1, i);
                if (isLetter(s))
                    preHalf = s + preHalf;
                else
                    break;
                i--;
            }
            // 后项扫描
            i = beginPos + 1;
            while (i < content.length()) {
                s = content.substring(i, i + 1);
                if (isLetter(s))
                    sufHalf = sufHalf + s;
                else
                    break;
                i++;
            }
            // 判断合法性
            email = preHalf + "@" + sufHalf;
            if (isEmail(email)) {
                return email;
            }
        }
        return null;
    }



    public static boolean isEmail(String email) {
        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(email).matches();
    }



    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
        return pattern.matcher(str).matches();
    }



    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }



    public static boolean isPrime(int x) {
        if (x <= 7) {
            if (x == 2 || x == 3 || x == 5 || x == 7)
                return true;
        }
        int c = 7;
        if (x % 2 == 0)
            return false;
        if (x % 3 == 0)
            return false;
        if (x % 5 == 0)
            return false;
        int end = (int) Math.sqrt(x);
        while (c <= end) {
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 6;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 6;
        }
        return true;
    }



    public static String hangeToBig(String str) {
        double value;
        try {
            value = Double.parseDouble(str.trim());
        } catch (Exception e) {
            return null;
        }
        char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
        char[] vunit = { '万', '亿' }; // 段名表示
        char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
        long midVal = (long) (value * 100); // 转化成整形
        String valStr = String.valueOf(midVal); // 转化成字符串

        String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
        String rail = valStr.substring(valStr.length() - 2); // 取小数部分

        String prefix = ""; // 整数部分转化的结果
        String suffix = ""; // 小数部分转化的结果
        // 处理小数点后面的数
        if (rail.equals("00")) { // 如果小数部分为0
            suffix = "整";
        } else {
            suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
        }
        // 处理小数点前面的数
        char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
        char zero = '0'; // 标志'0'表示出现过0
        byte zeroSerNum = 0; // 连续出现0的次数
        for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
            int idx = (chDig.length - i - 1) % 4; // 取段内位置
            int vidx = (chDig.length - i - 1) / 4; // 取段位置
            if (chDig[i] == '0') { // 如果当前字符是0
                zeroSerNum++; // 连续0次数递增
                if (zero == '0') { // 标志
                    zero = digit[0];
                } else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
                    prefix += vunit[vidx - 1];
                    zero = '0';
                }
                continue;
            }
            zeroSerNum = 0; // 连续0次数清零
            if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
                prefix += zero;
                zero = '0';
            }
            prefix += digit[chDig[i] - '0']; // 转化该数字表示
            if (idx > 0)
                prefix += hunit[idx - 1];
            if (idx == 0 && vidx > 0) {
                prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
            }
        }

        if (prefix.length() > 0)
            prefix += '圆'; // 如果整数部分存在,则有圆的字样
        return prefix + suffix; // 返回正确表示
    }



    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    private static String removeSameString(String str) {
        Set mLinkedSet = new LinkedHashSet();// set集合的特征：其子集不可以重复
        String[] strArray = str.split(" ");// 根据空格(正则表达式)分割字符串
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < strArray.length; i++) {
            if (!mLinkedSet.contains(strArray[i])) {
                mLinkedSet.add(strArray[i]);
                sb.append(strArray[i] + " ");
            }
        }
        System.out.println(mLinkedSet);
        return sb.toString();
    }



    public static String encoding(String src) {
        if (src == null)
            return "";
        StringBuilder result = new StringBuilder();
        if (src != null) {
            src = src.trim();
            for (int pos = 0; pos < src.length(); pos++) {
                switch (src.charAt(pos)) {
                case '"':
                    result.append("\"");
                    break;
                case '<':
                    result.append("<");
                    break;
                case '>':
                    result.append(">");
                    break;
                case '\'':
                    result.append("'");
                    break;
                case '&':
                    result.append("&");
                    break;
                case '%':
                    result.append("&pc;");
                    break;
                case '_':
                    result.append("&ul;");
                    break;
                case '#':
                    result.append("&shap;");
                    break;
                case '?':
                    result.append("&ques;");
                    break;
                default:
                    result.append(src.charAt(pos));
                    break;
                }
            }
        }
        return result.toString();
    }



    public static boolean isHandset(String handset) {
        try {
            String regex = "^1[\\d]{10}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(handset);
            return matcher.matches();

        } catch (RuntimeException e) {
            return false;
        }
    }



    public static String decoding(String src) {
        if (src == null)
            return "";
        String result = src;
        result = result.replace("\"", "\"").replace("\'", "\'");
        result = result.replace("<", "<").replace(">", ">");
        result = result.replace("&", "&");
        result = result.replace("&pc;", "%").replace("&ul", "_");
        result = result.replace("&shap;", "#").replace("&ques", "?");
        return result;
    }



    /**
     * 
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        if (isBlank(str1) || isBlank(str2)) {
            return false;
        }
        return str1.equals(str2);
    }



    /**
     * 
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (isBlank(str1) || isBlank(str2)) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }



    public static String getPingYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else {
                    t4 += java.lang.Character.toString(t1[i]);
                }
            }
            return t4;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return t4;
    }



    public static String getPinYinHeadChar(String str) {

        String convert = "";
        String[] pinyinArray = null;
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            try {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word, new HanyuPinyinOutputFormat());
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }



    /**
     * 
     * @Title: getFirstTextHeadChar
     * @Description:获取字符串第一个字的首字母
     * @param @param str
     * @param @return
     * @return String
     * @throws
     */
    public static String getFirstTextHeadChar(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        str = str.substring(0, 1);
        return getPinYinHeadChar(str);
    }



    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }



    /**
     * 
     * @Title: getStringList
     * @Description:将(逗号)分隔的字符串 将分隔成list
     * @param @param str
     * @param @return
     * @return List<String>
     * @throws
     */
    public static List<String> getStringList(String str, String separator) {
        if (isBlank(str)) {
            return null;
        }
        String[] strArray = str.split(separator);
        List<String> strList = new ArrayList<String>();
        for (String string : strArray) {
            strList.add(string.trim());
        }
        return strList;
    }



    /**
     * 
     * @Title: getStringList
     * @Description:将(逗号)分隔的字符串 将分隔成list
     * @param @param str
     * @param @return
     * @return List<String>
     * @throws
     */
    public static List<Long> getLongList(String str, String separator) {
        if (isBlank(str)) {
            return null;
        }
        String[] strArray = str.split(separator);
        List<Long> strList = new ArrayList<Long>();
        for (String string : strArray) {
            strList.add(Long.parseLong(string.trim()));
        }
        return strList;
    }



    /**
     * 
     * @Title: getIntByRandom
     * @Description:获取指定范围的整数，指定返回个数，不重复
     * @param @param count
     * @param @param range
     * @param @return
     * @return int[]
     * @throws
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static int[] getIntByRandom(int count, int range) {
        int[] result = new int[count];
        Integer[] resultInteger = new Integer[count];
        Set numberSet = new HashSet();
        for (; numberSet.size() < count;) {
            Random rand = new Random();
            numberSet.add(rand.nextInt(range));
        }
        numberSet.toArray(resultInteger);
        for (int i = 0; i < result.length; i++) {
            result[i] = resultInteger[i];
        }
        return result;
    }



    /**
     * 
     * @Title: saveTwoDecimal
     * @Description:保留两位小数
     * @param @param price
     * @param @return
     * @return double
     * @throws
     */
    public static String savePriceTwoDecimal(double price) {
        DecimalFormat df = new DecimalFormat(".##");
        String priceStr = df.format(price);
        String[] priceStrArry = priceStr.split("\\.");
        if (!CollectionsUtils.isNull(priceStrArry) && priceStrArry.length == 2 && priceStrArry[1].length() == 1) {
            priceStr = priceStr + "0";
        }
        if (!CollectionsUtils.isNull(priceStrArry) && priceStrArry.length == 2 && StringUtils.isBlank(priceStrArry[0])) {
            priceStr = "0" + priceStr;
        }
        return priceStr;
    }



    /**
     * 
     * @Title: formatStringByRegex
     * @Description:根据正则表达式，格式化数据 13812345678 根据 $1****$3 ->138****5678
     * @param @param value
     * @param @param format
     * @param @return
     * @return String
     * @throws
     */
    public static String formatStringByRegex(String value, String format) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(format)) {
            return value;
        }
        String[] regexArray = format.split(ContentUtils.COMMA);
        String regex = regexArray[0];
        String valueFormat1 = regexArray[1];

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            value = value.replaceAll(regex, valueFormat1);
        } else {
            if (regexArray.length == 3) {
                String valueFormat2 = regexArray[2];
                value = value.replaceAll(regex, valueFormat2);
            } else {
                value = "";
            }
        }
        return value;
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
        if (!StringUtils.isBlank(str)) {
            str = str.replaceAll(ContentUtils.COMMA, "");
            return Double.parseDouble(str);
        }
        return 0;
    }



    /**
     * 
     * @Title: equalsByRegex
     * @Description:根据正则表达式 匹配字符串
     * @param @param value
     * @param @param regex
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean matchByRegex(String value, String regex) {
        // 正则表达式匹配
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }



    /**
     * 截取数字
     * 
     * @param content
     * @return
     */
    public static Long getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return Long.parseLong(matcher.group(0));
        }
        return null;
    }



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
     * 
     * @Title: decode
     * @Description: 处理编码 和特殊字符(% +)
     * @param @param data
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public static String decode(String data) {
        try {
            while (data.contains("%") || data.contains("+")) {
                if (data.contains("%")) {
                    data = data.replaceAll("%", "<percentage>");
                }
                if (data.contains("+")) {
                    data = data.replaceAll("+", "<plus>");
                }
            }
            data = URLDecoder.decode(data, "utf-8");
            data = data.replaceAll("<percentage>", "%");
            data = data.replaceAll("<plus>", "+");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
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
}
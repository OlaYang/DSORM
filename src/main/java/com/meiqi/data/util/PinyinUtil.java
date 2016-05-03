package com.meiqi.data.util;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.user.handler.service.ServiceRespInfo;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-11-12
 * Time: 下午4:41
 * To change this template use File | Settings | File Templates.
 */
public class PinyinUtil {
    private static Map<String, String> zh2pinyin = null;  // 系统变量中配置 中文2pinyin
    private static List<String> zh = null;   // 系统变量中配置 中文

    private static void loadPinyin() throws Exception {
        if (zh2pinyin == null || zh == null) {
            zh2pinyin = new ConcurrentHashMap<String, String>();
            zh = new ArrayList<String>();
            String[] pinyinSystemValueArray = getPinyinSystemVariableValue();
            for (int i = 0; i < pinyinSystemValueArray.length; i += 2) {
                if (i + 1 >= pinyinSystemValueArray.length) {
                    break;
                }
                zh.add(pinyinSystemValueArray[i]);
                zh2pinyin.put(pinyinSystemValueArray[i], pinyinSystemValueArray[i + 1]);
            }
        }
    }


    private static void initPinyin() {
        try {
            loadPinyin();
        } catch (Exception e) {
            LogUtil.error("初始化pinyin系统变量失败", e);
        }
    }

    public static void reloadPinyin() {
        try {
            zh2pinyin = null;
            zh = null;
            loadPinyin();
        } catch (Exception e) {
            LogUtil.error("重载pinyin系统变量失败", e);
        }
    }

    private static String[] getPinyinSystemVariableValue() throws Exception {

        ServiceRespInfo respInfo = JSON.parseObject(Tool.getSystemVariables(Tool.systemVariablesName.RULE_CONVERT_PINYIN), ServiceRespInfo.class);
        List<Map<String, String>> rows = respInfo.getRows();
        if (rows != null && rows.size() == 1) {
            Map<String, String> row = rows.get(0);
            String varValue = row.get("变量值");
            if (varValue != null) {
                String[] varValueArray = varValue.split(",");
                return varValueArray;
            }
        }
        return null;
    }


    /**
     * 获取拼音集合
     *
     * @param src
     * @return Set<String>
     * @author wangkun
     */
    private static Map<String, Set<String>> getPinyin(String src) {
        if (src != null && !src.trim().equalsIgnoreCase("")) {
            char[] srcChar;
            srcChar = src.toCharArray();
            // 汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

            // 输出设置，大小写，音标方式等
            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            String[][] pingyinTemp = new String[src.length()][];
            for (int i = 0; i < srcChar.length; i++) {
                char c = srcChar[i];
                // 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                    try {
                        pingyinTemp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hanYuPinOutputFormat);
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        LogUtil.error("pinyin4j编译失败:", e);
                    }
                } else if (String.valueOf(c).matches("[\\w\\?%&=\\-_#*.]")) {
                    pingyinTemp[i] = new String[]{String.valueOf(srcChar[i])};
                } else {
                    pingyinTemp[i] = new String[]{""};
                }
            }
            String[] pingyinArray = Exchange(pingyinTemp);
            Set<String> pinyinSet = new HashSet<String>();
            for (int i = 0; i < pingyinArray.length; i++) {
                pinyinSet.add(pingyinArray[i]);
            }
            Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
            resultMap.put("pinyin", pinyinSet);
            return resultMap;
        }
        return null;
    }

    /**
     * 递归
     *
     * @param strJaggedArray
     * @return
     * @author wangkun
     */
    private static String[] Exchange(String[][] strJaggedArray) {
        String[][] temp = DoExchange(strJaggedArray);
        return temp[0];
    }

    /**
     * 递归
     *
     * @param strJaggedArray
     * @return
     * @author wangkun
     */
    private static String[][] DoExchange(String[][] strJaggedArray) {
        int len = strJaggedArray.length;
        if (len >= 2) {
            int len1 = strJaggedArray[0].length;
            int len2 = strJaggedArray[1].length;
            int newlen = len1 * len2;
            String[] temp = new String[newlen];
            int Index = 0;
            for (int i = 0; i < len1; i++) {
                for (int j = 0; j < len2; j++) {
                    temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
                    Index++;
                }
            }
            String[][] newArray = new String[len - 1][];
            for (int i = 2; i < len; i++) {
                newArray[i - 1] = strJaggedArray[i];
            }
            newArray[0] = temp;
            return DoExchange(newArray);
        } else {
            return strJaggedArray;
        }
    }

    /**
     * 字符串集合转换字符串(逗号分隔)
     *
     * @param stringSet
     * @param defaultFirst 是否默认获取第一个拼音
     * @return
     * @author wangkun
     */
    private static String makeStringByStringSet(Set<String> stringSet, boolean defaultFirst) {
        StringBuilder str = new StringBuilder();
        int i = 0;
        for (String s : stringSet) {
            if (i == stringSet.size() - 1) {
                str.append(s);
                break;
            } else {
                str.append(s);
                if (defaultFirst && i == 0) {
                    break;
                } else {
                    str.append(",");
                }
            }
            i++;
        }
        return str.toString().toLowerCase();
    }


    public static String getDefaultPinyin(String source) {
        initPinyin();
        for (int i = 0; i < zh.size(); i++) {
            source = source.replaceAll(zh.get(i), "," + zh.get(i) + ",");
        }
        String[] array = source.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (array[i].trim().equals("")) {
                //
            } else {
                if (zh2pinyin.get(array[i]) != null) {
                    sb.append(zh2pinyin.get(array[i]));
                } else {
                    Map<String, Set<String>> pinyinResultMap = getPinyin(array[i]);
                    sb.append(makeStringByStringSet(pinyinResultMap.get("pinyin"), true));
                }
            }
        }
        return sb.toString();
    }



}

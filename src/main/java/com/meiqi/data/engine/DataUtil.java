package com.meiqi.data.engine;


import static com.meiqi.data.engine.excel.NumberPool.DOUBLE_0;
import static com.meiqi.data.engine.excel.NumberPool.LONG_0;
import static com.meiqi.data.engine.excel.NumberPool.LONG_1;
import static com.meiqi.data.engine.excel.NumberPool.LONG_1000;
import static com.meiqi.data.engine.excel.NumberPool.LONG_M_1;
import static com.meiqi.data.engine.excel.NumberPool.parseDouble;
import static com.meiqi.data.engine.excel.StringPool.FALSE;
import static com.meiqi.data.engine.excel.StringPool.TRUE;
import static com.meiqi.data.util.Type.BOOLEAN;
import static com.meiqi.data.util.Type.DATE;
import static com.meiqi.data.util.Type.DOUBLE;
import static com.meiqi.data.util.Type.INTEGER;
import static com.meiqi.data.util.Type.LONG;
import static com.meiqi.data.util.Type.RANGE;
import static com.meiqi.data.util.Type.STRING;
import static com.meiqi.data.util.Type.BIGDECIMAL;
import static com.meiqi.data.util.Type.BIGINTEGER;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;
import com.meiqi.data.util.ConfigUtil;
import com.meiqi.data.util.DebugUtil;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.Type;

/**
 * User: 
 * Date: 13-6-21
 * Time: 上午10:49
 */
public class DataUtil {
    private static final Class
            CLASS_STRING = String.class, CLASS_LONG = Long.class, CLASS_INTEGER=Integer.class,
            CLASS_DATE = Date.class, CLASS_DOUBLE = Double.class, CLASS_BOOL = Boolean.class, 
            CLASS_RANGE = ExcelRange.class,CLASS_BIGDECIMAL=BigDecimal.class,
            CLASS_BIGINTEGER=BigInteger.class;
    public static final HashMap<String, Object> EMPTY = new HashMap<String, Object>(0);
    private static final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            NumberFormat tmp = NumberFormat.getInstance();
            tmp.setGroupingUsed(false);
            tmp.setMaximumFractionDigits(6);
            tmp.setRoundingMode(RoundingMode.HALF_EVEN);
            return tmp;
        }
    };


    private static final ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };


    static ConcurrentHashMap<String, AtomicLong> serviceTimes
            = new ConcurrentHashMap<String, AtomicLong>();
    static final int calTimeout = ConfigUtil.getCalTimeout();

    static {

    }

    public static void inheritParam(Map<String, Object> current, Map<String, Object> father) {
        for (Map.Entry<String, Object> entry : father.entrySet()) {
            if (!current.containsKey(entry.getKey())) {
                current.put(entry.getKey(), entry.getValue());
            }
        }
    }


    public static String getServiceName(Object input) throws RengineException {
        Type type = getType(input);
        String name = null;

        if (type == Type.LONG) {
            name = Services.id2Name(((Long) input).intValue());
        } else {
            name = DataUtil.getStringValue(input, type).trim();
        }

        if (name == null) {
            throw new RengineException(String.valueOf(input), "数据源未找到, " + input);
        }

        return name;
    }

    public static int getColumnIntIndex(String source, List<TServiceColumn> columnPos)
            throws RengineException {
        boolean isColumnIndex = isColumnIndex(source);

        if (isColumnIndex) {
            source = source.toUpperCase();
        }

        for (TServiceColumn columnPo : columnPos) {
            String tmp = isColumnIndex ? columnPo.getColumnIndex() : columnPo.getColumnName();
            if (tmp.equals(source)) {
                return columnPo.getColumnIntIndex();
            }
        }

        return -1;
    }

    public static boolean isColumnIndex(String source) {
        if (source.length() > 3) {
            return false;
        }

        for (int i = 0; i < source.length(); i++) {
            char tmp = source.charAt(i);
            if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
                //
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 数据源计算入口类, 计算对应数据源+参数的结果
     *
     * @param info
     * @param param
     * @return
     * @throws RengineException
     */
    public static D2Data getD2Data(TService info, Map<String, Object> param) throws RengineException {
        if (info == null) {
            return null;
        }

        if (param == null) {
            param = EMPTY;
        }

        //final Integer serviceID = info.getServiceID();
        final String name = info.getName();
        param = checkParam(param, name);
        clearTLS();

        //final String json = JSON.toJSONString(param);
        //final long time = System.currentTimeMillis();
        try {
            D2Data data = Cache4D2Data.getD2Data(info, param, 0, null, null, "起始");
            return data;
        } finally {
            if (name != null) {
                AtomicLong atomicLong = serviceTimes.get(name);
                if (atomicLong == null) {
                    serviceTimes.put(name, new AtomicLong(1));
                } else {
                    atomicLong.incrementAndGet();
                }
            }

//            final long lat = System.currentTimeMillis() - time;
//            LogUtil.info(serviceID + "-" + name + " " + json + " latency:" + lat);
//            if (lat > calTimeout) {
////                LogMonitor.error(name,  ";" + "数据源运算时间超过阀值(" + calTimeout + "), " + lat + ", 参数" + json );
//            }

            Cache4_O_.clear();
            Cache4D2Data.clear();
        }
    }


    public static Type getType(Object input) throws RengineException {
        if (input == null) {
            return Type.NULL;
        }

        final Class clazz = input.getClass();
        if (clazz == CLASS_STRING) {
            return STRING;
        } else if (clazz == CLASS_DOUBLE) {
            return DOUBLE;
        } else if (clazz == CLASS_LONG) {
            return LONG;
        } else if (clazz == CLASS_INTEGER) {
            return INTEGER;
        }else if (clazz == CLASS_BOOL) {
            return BOOLEAN;
        } else if (clazz == CLASS_DATE) {
            return DATE;
        } else if (clazz == CLASS_RANGE) {
            return RANGE;
        } else if (clazz == CLASS_BIGDECIMAL) {
            return BIGDECIMAL;
        } else if (clazz == CLASS_BIGINTEGER) {
            return BIGINTEGER;
        } else {
            throw new RengineException(null, "类型不支持, " + input.getClass().getSimpleName());
        }
    }


    /**
     * -1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compare(double d1, double d2) {
        return new BigDecimal(d1).setScale(6, BigDecimal.ROUND_HALF_EVEN).compareTo(
                new BigDecimal(d2).setScale(6, BigDecimal.ROUND_HALF_EVEN));
    }

    public static String getNextColumnIndex(String columnIndex) throws RengineException {
        int index = countIndex(columnIndex);
        return extract(index + 1);
    }

    public static String extract(int i) {
        i += 1;
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);


        while (i > 0) {
            final int j = (i % 26);
            sb.append("" + (char) ('A' + (j == 0 ? 25 : (j - 1))));
            i = (i - 1) / 26;
        }


        return sb.reverse().toString();
    }

    private static final ConcurrentHashMap<String, Integer> columnIndexMap
            = new ConcurrentHashMap<String, Integer>();

    public static int countIndex(String columnIndex) throws RengineException {
        if (columnIndex == null) {
            throw new RengineException(null, "columnIndex为空");
        }

        if (columnIndex.length() > 3) {
            throw new RengineException(null, "列位置长度最大为3, " + columnIndex);
        }

        Integer index = columnIndexMap.get(columnIndex);

        if (index == null) {
            columnIndex = columnIndex.toUpperCase();
            int count = 0;

            for (char c : columnIndex.toCharArray()) {
                if (c < 'A' || c > 'Z') {
                    throw new RengineException(null, "列位置非法, " + columnIndex);
                }

                count = count * 26 + (c - 'A' + 1);
            }

            index = count - 1;

            columnIndexMap.put(columnIndex, index);
        }

        return index;
    }

    public static boolean isValid(String columnIndex) {
        if (columnIndex.isEmpty()) {
            return false;
        }

        for (char c : columnIndex.toCharArray()) {
            if ((c >= 'A' && c <= 'Z')) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public static <T> T parse(String content, Class<T> clazz) throws RengineException {
        if (content == null || content.trim().length() == 0) {
            content = "{}";
        }

        try {
            return JSON.parseObject(content, clazz);
        } catch (Throwable e) {

            try {
                DebugUtil.debugJSON(e.getMessage(), content); // llcheng 对于json解析错误的加入json传入内容
            } catch (RengineException re) {
                throw re;
            } catch (Exception ex) {
                //
            }

            throw new RengineException(null, "JSON解析错误, " + e.getMessage());
        }
    }

    public static Map<String, Object> parse(String content) throws RengineException {
        if (content == null || content.trim().length() == 0) {
            content = "{}";
        }

        try {
            return JSON.parseObject(content);
        } catch (Throwable e) {
            try {
                DebugUtil.debugJSON(e.getMessage(), content); // llcheng 对于json解析错误的加入json传入内容
            } catch (RengineException re) {
                throw re;
            } catch (Exception ex) {
                //
            }

            throw new RengineException(null, "JSON解析错误, " + e.getMessage());
        }
    }

    public static String getEncryptedPassword(String source) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(source.getBytes());

        byte[] data = md.digest();
        char[] buff = new char[data.length * 2];
        int tmp;

        for (int i = 0; i < data.length; i++) {
            tmp = ((data[i] >> 4) & 0xf);
            buff[i * 2] = tmp < 10 ? (char) ('0' + tmp) : (char) ('a' + tmp - 10);
            tmp = data[i] & 0xf;
            buff[i * 2 + 1] = tmp < 10 ? (char) ('0' + tmp) : (char) ('a' + tmp - 10);
        }

        return new String(buff);
    }

    /**
     * 清除线程局部缓存, 包括跨表函数, 数据源局部缓存, 运算信息的缓存
     */
    private static void clearTLS() {
        if (LogUtil.isDebugEnabled()) {
            LogUtil.debug("clear TLS");
        }
        Cache4_O_.clear();
        Cache4D2Data.clear();
        ProcessInfos.clear();
    }

    public static String date2String(Date date) {
        if (date == null) {
            return null;
        }

        return simpleDateFormat.get().format(date);
    }

    public static String number2String(double d) {
        final String ret = numberFormat.get().format(d);

        if (ret.length() == 2 && ret.equals("-0")) {
            return "0";
        }

        return ret;
    }

    public static String getStringValue(Object input) throws RengineException {
        return getStringValue(input, getType(input));
    }

    public static String getStringValue(Object input, Type type) {
        String result = StringPool.EMPTY;

        switch (type) {
            case LONG:
                result = Long.toString((Long) input);
                break;  
           case INTEGER:
                result = Integer.toString((Integer) input);
                break;
            case DOUBLE:
                result = number2String((Double) input);
                break;
            case BOOLEAN:
                result = (Boolean) input ? TRUE : FALSE;
                break;
            case DATE:
                result = date2String((Date) input);
                break;
            case STRING:
                result = ((String) input);
                break;
            case BIGDECIMAL:
                result = ((BigDecimal) input).toString();
                break;
            case BIGINTEGER:
                result = ((BigInteger) input).toString();
                break;
        }

        return result;
    }

    public static Number getNumberValue(Object input) throws RengineException, CalculateError {
        return getNumberValue(input, getType(input));
    }

    public static Number getNumberValue(Object input, Type type) throws CalculateError {
        Number result = LONG_0;

        switch (type) {
            case LONG:
                result = (Long) input;
                break;
            case DOUBLE:
                result = (Double) input;
                break;
            case BOOLEAN:
                result = (Boolean) input ? LONG_1 : LONG_0;
                break;
            case DATE:
                result = ((Date) input).getTime() / LONG_1000;
                break;
            case STRING:
                result = parseDouble((String) input);
                break;
            case BIGDECIMAL:
                result = new BigDecimal(input.toString());
                break; 
            case BIGINTEGER:
                result = new BigInteger(input.toString());
                break; 
        }

        return result;
    }


    /**
     * 判断参数是否全为空，如果全为空则报错，map会认为是不为空
     *
     * @param param
     * @param name
     */
    private static Map<String, Object> checkParam(Map<String, Object> param, String name)
            throws RengineException {
        if (param.isEmpty()) {
            return param;
        }

        boolean isAllEmpty = true;
        int maxParamSize = 0;


        for (Map.Entry<String, Object> entry : param.entrySet()) {
            final Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            if (value instanceof Map) {
                isAllEmpty = false;
                int size = ((Map) value).size();
                maxParamSize = size > maxParamSize ? size : maxParamSize;
            } else if (value.toString().length() != 0) {
                isAllEmpty = false;
            }
        }

        if (isAllEmpty) {
            throw new RengineException(name, "输入参数全为空");
        }

        if (param.get("datarow_need") == null) {
            param.put("datarow_need", maxParamSize);
        }

        param = convert2String(param);
        return param;
    }

    public static Map<String, Object> convert2String(Map<String, Object> param) {
        Map<String, Object> retValue = new HashMap<String, Object>(param.size());

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }

            String key2 = entry.getKey();
            Object value = entry.getValue();

            if (value != null) {
                if (value instanceof Map) {
                    retValue.put(key2, convert2String((Map) value));
                } else if (value instanceof List) {
                    retValue.put(key2, convert2String((List) value));
                } else {
                    retValue.put(key2, value.toString());
                }
            }
        }

        return retValue;
    }


    public static List<Object> convert2String(List<Object> param) {
        List<Object> retValue = new ArrayList<Object>(param.size());

        for (Object value : param) {
            if (value != null) {
                if (value instanceof Map) {
                    retValue.add(convert2String((Map) value));
                } else if (value instanceof List) {
                    retValue.add(convert2String((List) value));
                } else {
                    retValue.add(value.toString());
                }
            }
        }

        return retValue;
    }

    public static Object getValueEval(Object input) throws RengineException {
        Type type = getType(input);

        switch (type) {
            case BOOLEAN:
            case STRING:
            case DOUBLE:
            case LONG:
            case NULL:
                return input;
            case DATE:
                return new String(date2String((Date) input));
            default:
                return input;
        }
    }

    public static long compareO(Object left, Object right) throws RengineException, CalculateError {
        if (left == null || right == null) {
            if (left != null) {
                Type leftType = getType(left);

                if (leftType == Type.STRING) {
                    if (DataUtil.getStringValue(left, leftType).length() == 0) {
                        return NumberPool.LONG_0;
                    }
                } else if (leftType == Type.LONG) {
                    if (((Long) left) == LONG_0) {
                        return LONG_0;
                    }
                } else if (leftType == Type.DOUBLE) {
                    if (DataUtil.compare((Double) left, DOUBLE_0) == 0) {
                        return LONG_0;
                    }
                }

                return LONG_1;
            }

            if (right != null) {
                Type rightType = getType(right);

                if (rightType == Type.STRING) {
                    if (DataUtil.getStringValue(right, rightType).length() == 0) {
                        return NumberPool.LONG_0;
                    }
                } else if (rightType == Type.LONG) {
                    if (((Long) right) == LONG_0) {
                        return LONG_0;
                    }

                } else if (rightType == Type.DOUBLE) {
                    if (DataUtil.compare((Double) right, DOUBLE_0) == 0) {
                        return LONG_0;
                    }
                }

                return LONG_M_1;
            }

            return LONG_0;
        } else {
            Type leftType = getType(left);
            Type rightType = getType(right);

            if ((leftType == Type.STRING || leftType == Type.DATE)
                    && (rightType == Type.STRING || rightType == Type.DATE)) {
                return (getStringValue(left, leftType)).compareTo(getStringValue(right, rightType));
            } else {
                if (leftType == Type.STRING) {
                    return 1;
                }

                if (rightType == Type.STRING) {
                    return -1;
                }

                Number leftN = getNumberValue(left, leftType);
                Number rightN = getNumberValue(right, rightType);

                if (leftType == Type.DOUBLE || rightType == Type.DOUBLE) {
                    return DataUtil.compare(leftN.doubleValue(), rightN.doubleValue());
                }

                return leftN.longValue() - rightN.longValue();
            }
        }
    }


    public static String getColumnName(List<TServiceColumnPo> columnPos, Integer index) throws RengineException {
        if (columnPos.size() <= index) {
            return StringPool.EMPTY;
        }
        return columnPos.get(index).getColumnName();
    }


    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String inputStream2String(InputStream inputStream) throws IOException {
        byte[] buff = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.reset();
        int count;

        while ((count = inputStream.read(buff)) != -1) {
            baos.write(buff, 0, count);
        }

        return new String(baos.toByteArray());
    }

    public static String createHttpURL(String host, int port, String uri) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://");
        sb.append(host);
        sb.append(":");
        sb.append(port);
        sb.append("/");
        sb.append(uri);
        return sb.toString();
    }

    public static String createHttpURL(String host, String uri) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://");
        sb.append(host);
        sb.append("/");
        sb.append(uri);
        return sb.toString();
    }

    public static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator(){
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry)o1).getValue())
                        .compareTo(((Map.Entry)o2).getValue());
            }
        });
        Map result = new LinkedHashMap();

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map .Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;

    }
}

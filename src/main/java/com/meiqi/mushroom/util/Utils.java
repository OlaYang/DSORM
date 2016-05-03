package com.meiqi.mushroom.util;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 获取事务号、与json转换工具类
 * @author 	
 * @date 2015年6月18日
 */

public class Utils {
    private static final AtomicLong sid = new AtomicLong(0);

    private static final ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    //进行将json报文转换为jsonObejct解析
    public static <T> T parse(String content, Class<T> clazz) throws IllegalArgumentException {
    	
        if (null==content || "".equals(content.trim())) {
            content = "{}";
        }


        try {
            return JSON.parseObject(content, clazz);
        } catch (Throwable e) {
            throw new IllegalArgumentException("JSON解析错误, " + e.getMessage());
        }
    }

    //将num/list转换为sql格式String
    public static String getSqlString(Object obj) {
        if (obj instanceof Number) {
            return String.valueOf(obj);
        }

        if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            boolean first = true;

            for (Object value : list) {
                if (null==value) {
                    continue;
                }

                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }

                if (value instanceof Number) {
                    sb.append(String.valueOf(obj));
                } else {
                    String tmp = String.valueOf(value);

                    if ("null".equals(tmp)) {
                        sb.append("NULL");
                    } else {
                        sb.append("'").append(tmp.replace("'", "\\'")).append("'");
                    }

                }
            }
            sb.append(")");
            return sb.toString();
        }

        return "'" + String.valueOf(obj).replace("'", "\\'") + "'";
    }

    //获取一个事务编号
    public static String getTransactionNum() {
    	//获取当前毫秒
        final long time = System.currentTimeMillis(); 
        // 以原子方式将当前值加 1，并返回加1前的值。等价于“num++”
        final long id = sid.getAndIncrement();
        //拼装事务编号，保证事务唯一性
        return "T-" + time + "-" + (id & 0xFFFF);
    }

    //日期转换为String
    public static String date2String(Date date) {
        if (date == null) {
            return null;
        }
        //返回一个yyyy-MM-dd HH:mm:ss格式的字符串
        return simpleDateFormat.get().format(date);
    }
    
    
}

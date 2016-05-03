package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-25
 * Time: 下午8:07
 * To change this template use File | Settings | File Templates.
 */
public class SPLIT_STR_JSON extends Function {
    //字符串拆分转json函数，例如: a,b,c,d --> {"1":"a","2":"b","3":"c","4":"d"}
    static final String NAME = SPLIT_STR_JSON.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }
        String str = DataUtil.getStringValue(args[0]);
        String split = ",";
        if (args.length == 2) {
            split = DataUtil.getStringValue(args[1]);
        }
        split = "\\" + split;
        _Key key = new _Key(str, split);
        Map<Object, Object> cache = calInfo.getCache(NAME);
        String result = (String) cache.get(key);
        if (result == null) {
            String[] array = str.split(split);
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (int i = 0; i < array.length; i++) {
                map.put(String.valueOf(i + 1), array[i].trim());
            }
            result = JSON.toJSONString(map);
            cache.put(key, result);
        }
        return result;
    }

    class _Key {
        Object str;
        Object split;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof _Key)) return false;

            _Key key = (_Key) o;

            if (split != null ? !split.equals(key.split) : key.split != null)
                return false;
            if (str != null ? !str.equals(key.str) : key.str != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = str != null ? str.hashCode() : 0;
            result = 31 * result + (split != null ? split.hashCode() : 0);
            return result;
        }

        _Key(Object str, Object split) {
            this.str = str;
            this.split = split;
        }
    }

    public static void main(String args[]) {
        String split = ",";
        split = "\\" + split;
        String str = "a,b,c,d";
        String array[] = str.split(split);
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }
}

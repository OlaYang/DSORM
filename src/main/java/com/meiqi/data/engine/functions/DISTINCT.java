package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-5-16
 * Time: 上午9:44
 * To change this template use File | Settings | File Templates.
 */
public class DISTINCT extends Function {
    static final String NAME = DISTINCT.class.getSimpleName();

    //去重函数,返回去重后的数列
    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }
        Set<Object> set = new LinkedHashSet<Object>();
        Map<Object, Object> cache = calInfo.getCache(NAME);
        List<Object> argList = new ArrayList<Object>();
        for (int i = 0; i < args.length; i++) {
            argList.add(args[i]);
        }
        __Key key = new __Key(argList);
        ExcelRange result = (ExcelRange) cache.get(key);
        if (result == null) {
            for (Object arg : args) {
                if (arg == null) {
                    continue;
                }
                if (arg instanceof ExcelRange) {
                    ExcelRange range = (ExcelRange) arg;
                    Set<Object> rangeSet = (Set<Object>) cache.get(range);
                    if (rangeSet == null) {
                        rangeSet = new LinkedHashSet<Object>();
                        Iterator<Object> ite = range.getIterator();
                        while (ite.hasNext()) {
                            Object value = ite.next();
                            if (value != null) {
                                rangeSet.add(value);
                            }
                        }
                        cache.put(range, rangeSet);
                    }
                    set.addAll(rangeSet);
                } else if (arg != null) {
                    set.add(arg);
                }
            }

            Object[] values = set.toArray();
            Object[][] data = new Object[values.length][1];
            for (int i = 0; i < values.length; i++) {
                data[i][0] = values[i];
            }
            D2Data d2Data = new D2Data(null);
            d2Data.setData(data);
            result = new ExcelRange(0, 0, 0, values.length, d2Data);
            cache.put(key, result);
        }
        return result;
    }


    class __Key {
        List<Object> argList;      // 由于数列有顺序，故需要用List作为key

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (argList != null ? !argList.equals(key.argList) : key.argList != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return argList != null ? argList.hashCode() : 0;
        }

        public __Key(List argList) {
            this.argList = argList;
        }
    }
}

package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class MINBYGROUP extends Function {
    static final String NAME = MINBYGROUP.class.getSimpleName();

    class __Key {
        Object by, cal;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (by != null ? !by.equals(key.by) : key.by != null) return false;
            if (cal != null ? !cal.equals(key.cal) : key.cal != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = by != null ? by.hashCode() : 0;
            result = 31 * result + (cal != null ? cal.hashCode() : 0);
            return result;
        }

        __Key(Object by, Object cal) {
            this.by = by;
            this.cal = cal;
        }
    }

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 3) {
            throw new ArgsCountError(NAME);
        }

        if (args[1] instanceof ExcelRange && args[2] instanceof ExcelRange) {
            String req = DataUtil.getStringValue(args[0]);
            ExcelRange by = (ExcelRange) args[1];
            ExcelRange cal = (ExcelRange) args[2];

            __Key key = new __Key(by, cal);
            Map<Object, Object> cache = calInfo.getCache(NAME);
            Map<String, Double> result = (Map<String, Double>) cache.get(key);

            if (result == null) {
                result = new HashMap<String, Double>();

                Iterator<Object> byI = by.getIterator();
                Iterator<Object> calI = cal.getIterator();

                while (byI.hasNext() && calI.hasNext()) {
                    final Object byObj = byI.next();
                    final Object calObj = calI.next();

                    if (byObj == null || calObj == null) {
                        continue;
                    }

                    if (canNumberOP(calObj)) {
                        String _key = DataUtil.getStringValue(byObj);
                        double value = DataUtil.getNumberValue(calObj).doubleValue();

                        Double oldValue = result.get(_key);
                        if (oldValue == null || oldValue > value) {
                            result.put(_key, value);
                        }
                    }
                }

                cache.put(key, result);
            }

            Double ret = result.get(req);
            if (ret == null) {
                return NumberPool.DOUBLE_0;
            }

            return ret;
        }

        throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
    }
}

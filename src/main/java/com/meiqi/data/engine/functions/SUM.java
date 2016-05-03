package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.Iterator;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-17
 * Time: 下午1:54
 */
public class SUM extends Function {
    static final String NAME = SUM.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length == 0) {
            return NumberPool.LONG_0;
        }

        double sum = NumberPool.DOUBLE_0;

        for (Object arg : args) {
            if (arg instanceof ExcelRange) {
                ExcelRange range = (ExcelRange) arg;

                Map<Object, Object> cache = calInfo.getCache(NAME);

                Double result = (Double) cache.get(range);
                if (result == null) {
                    Iterator<Object> ite = range.getIterator();

                    result = NumberPool.DOUBLE_0;

                    while (ite.hasNext()) {
                        Object tmp = ite.next();
                        if (tmp != null && canNumberOP(tmp)) {
                            result += DataUtil.getNumberValue(tmp).doubleValue();
                        }
                    }

                    cache.put(range, result);
                }

                sum += result;
            } else if (arg != null && canNumberOP(arg)) {
                sum += DataUtil.getNumberValue(arg).doubleValue();
            }
        }

        return sum;
    }
}

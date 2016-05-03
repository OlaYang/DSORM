package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.Iterator;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-17
 * Time: 下午6:30
 */
public class COUNTA extends Function {
    static final String NAME = COUNTA.class.getSimpleName();


    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        long count = NumberPool.LONG_0;

        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            if (arg instanceof ExcelRange) {
                ExcelRange range = (ExcelRange) arg;

                Map<Object, Object> cache = calInfo.getCache(NAME);
                Long result = (Long) cache.get(range);

                if (result == null) {
                    Iterator<Object> ite = range.getIterator();

                    result = NumberPool.LONG_0;

                    while (ite.hasNext()) {
                        Object value = ite.next();
                        if (value != null) {
                            result++;
                        }
                    }

                    cache.put(range, result);
                }

                count += result;
            } else {
                count++;
            }
        }

        return count;
    }
}
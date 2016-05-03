package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
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
public class COUNTBLANK extends Function {
    static final String NAME = COUNTBLANK.class.getSimpleName();


    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }

        if (args[0] instanceof ExcelRange) {
            ExcelRange range = (ExcelRange) args[0];

            Map<Object, Object> cache = calInfo.getCache(NAME);
            Long result = (Long) cache.get(range);

            if (result == null) {
                Iterator<Object> ite = range.getIterator();
                result = NumberPool.LONG_0;

                while (ite.hasNext()) {
                    Object value = ite.next();
                    if (value == null) {
                        result++;
                    }
                }

                cache.put(range, result);
            }

            return result;
        }

        throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
    }
}
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
 * Time: 下午6:30
 */
public class MIN extends Function {
    static final String NAME = MIN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length == 0) {
            return NumberPool.DOUBLE_0;
        }

        boolean hasMin = false;
        double min = Double.MAX_VALUE;

        for (Object arg : args) {
            if (arg instanceof ExcelRange) {
                Map<Object, Object> cache = calInfo.getCache(NAME);

                Object result = cache.get(arg);

                if (result == null) {
                    Iterator<Object> ite = ((ExcelRange) arg).getIterator();

                    boolean hasMinInRange = false;
                    double minInRange = Double.MAX_VALUE;
                    while (ite.hasNext()) {
                        Object tmp = ite.next();

                        if (tmp != null && canNumberOP(tmp)) {
                            hasMinInRange = true;
                            double tmpD = DataUtil.getNumberValue(tmp).doubleValue();
                            minInRange = tmpD < minInRange ? tmpD : minInRange;
                        }
                    }

                    if (hasMinInRange) {
                        result = minInRange;
                        cache.put(arg, minInRange);
                    } else {
                        result = false;
                        cache.put(arg, false);
                    }
                }

                if (!(result instanceof Boolean)) {
                    hasMin = true;
                    min = ((Double) result) < min ? ((Double) result) : min;
                }
            } else if (arg != null && canNumberOP(arg)) {
                hasMin = true;
                double tmpD = DataUtil.getNumberValue(arg).doubleValue();
                min = tmpD < min ? tmpD : min;
            }
        }

        if (hasMin) {
            return min;
        }

        return NumberPool.DOUBLE_0;
    }
}

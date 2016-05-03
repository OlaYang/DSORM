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
public class MAX extends Function {
    static final String NAME = MAX.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length == 0) {
            return NumberPool.DOUBLE_0;
        }

        boolean hasMax = false;
        double max = Long.MIN_VALUE;

        for (Object arg : args) {
            if (arg instanceof ExcelRange) {
                Map<Object, Object> cache = calInfo.getCache(NAME);

                Object result = cache.get(arg);

                if (result == null) {
                    Iterator<Object> ite = ((ExcelRange) arg).getIterator();

                    boolean hasMaxInRange = false;
                    double maxInRange = Long.MIN_VALUE;
                    while (ite.hasNext()) {
                        Object tmp = ite.next();

                        if (tmp != null && canNumberOP(tmp)) {
                            hasMaxInRange = true;
                            double tmpD = DataUtil.getNumberValue(tmp).doubleValue();
                            maxInRange = tmpD > maxInRange ? tmpD : maxInRange;
                        }
                    }

                    if (hasMaxInRange) {
                        result = maxInRange;
                        cache.put(arg, maxInRange);
                    } else {
                        result = false;
                        cache.put(arg, false);
                    }
                }

                if (!(result instanceof Boolean)) {
                    hasMax = true;
                    max = ((Double) result) > max ? ((Double) result) : max;
                }
            } else if (arg != null && canNumberOP(arg)) {
                hasMax = true;
                double tmpD = DataUtil.getNumberValue(arg).doubleValue();
                max = tmpD > max ? tmpD : max;
            }
        }

        if (hasMax) {
            return max;
        }

        return NumberPool.DOUBLE_0;
    }
}

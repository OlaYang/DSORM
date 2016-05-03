package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;

import java.util.*;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class LARGE extends Function {
    static final String NAME = LARGE.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        if (args[0] instanceof ExcelRange) {
            ExcelRange range = (ExcelRange) args[0];
            int index = DataUtil.getNumberValue(args[1]).intValue();

            Map<Object, Object> cache = calInfo.getCache(NAME);
            List<Double> result = (List<Double>) cache.get(range);

            if (result == null) {
                Iterator<Object> ite = range.getIterator();
                result = new ArrayList<Double>();

                while (ite.hasNext()) {
                    Object tmp = ite.next();

                    if (tmp != null && canNumberOP(tmp)) {
                        result.add(DataUtil.getNumberValue(tmp).doubleValue());
                    }
                }
                Collections.sort(result);
                Collections.reverse(result);
                cache.put(range, result);
            }

            try {
                return result.get(index - 1);
            } catch (Exception e) {
                throw new CalculateError("LARGE位置" + index + "非法");
            }
        }

        throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
    }
}

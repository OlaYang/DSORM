package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class CEILING extends Function {
    static final String NAME = CEILING.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        double number = DataUtil.getNumberValue(args[0]).doubleValue();
        double significance = DataUtil.getNumberValue(args[1]).doubleValue();
        if (DataUtil.compare(significance, NumberPool.DOUBLE_0) == NumberPool.LONG_0) {
            return NumberPool.LONG_0;
        }

        return significance * (Math.ceil(number / significance));
    }
}

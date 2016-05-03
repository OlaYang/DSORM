package com.meiqi.data.engine.functions;

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
public final class LN extends Function {
    static final String NAME = LN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            return Math.log(DataUtil.getNumberValue(args[0]).doubleValue());
        }

        return NumberPool.LONG_0;
    }
}

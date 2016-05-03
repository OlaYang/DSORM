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
public final class SIGN extends Function {
    static final String NAME = SIGN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            final double d = DataUtil.getNumberValue(args[0]).doubleValue();
            return new Long(DataUtil.compare(d, NumberPool.DOUBLE_0));
        }

        throw new ArgsCountError(NAME);
    }
}

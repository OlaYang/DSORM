package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class TAN extends Function {
    static final String NAME = TAN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }

        Number number = DataUtil.getNumberValue(args[0]);
        return Math.tan(number.doubleValue());
    }
}

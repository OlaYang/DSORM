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
public final class ISEVEN extends Function {
    static final String NAME = ISEVEN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            return DataUtil.getNumberValue(args[0]).intValue() % 2 == 0;
        }

        throw new ArgsCountError(NAME);
    }
}

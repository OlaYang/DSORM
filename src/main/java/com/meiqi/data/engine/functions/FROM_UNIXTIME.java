package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.Date;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class FROM_UNIXTIME extends Function {
    static final String NAME = FROM_UNIXTIME.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        long time = NumberPool.LONG_0;

        if (args.length > 0) {
            time = DataUtil.getNumberValue(args[0]).longValue();
        }

        return new Date(time * 1000);
    }
}

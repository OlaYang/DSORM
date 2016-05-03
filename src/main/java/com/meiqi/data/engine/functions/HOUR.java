package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.Calendar;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class HOUR extends Function {
    static final String NAME = HOUR.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(DataUtil.getNumberValue(args[0]).longValue() * 1000);

            return new Long(calendar.get(Calendar.HOUR_OF_DAY));
        }

        return NumberPool.LONG_0;
    }
}

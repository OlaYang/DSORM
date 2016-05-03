package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.util.Type;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class UNIXTIME extends Function {
    static final String NAME = UNIXTIME.class.getSimpleName();
    static final Function now = Function.getFunction("NOW");

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        Date date = null;
        String format = null;

        if (args.length > 0) {
            Type t = DataUtil.getType(args[0]);

            if (t == Type.DATE) {
                date = (Date) args[0];
            } else {
                long time = DataUtil.getNumberValue(args[0]).longValue();
                date = new Date(time * NumberPool.LONG_1000);
            }
        } else {
            date = (Date) now.eval(calInfo, args);
        }

        if (args.length > 1 && DataUtil.getType(args[1]) == Type.STRING) {
            format = (String) args[1];

            try {
                return new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
                //
            }
        }

        return date.getTime() / NumberPool.LONG_1000;
    }
}

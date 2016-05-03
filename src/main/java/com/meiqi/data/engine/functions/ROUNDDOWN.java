package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;

import java.math.BigDecimal;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class ROUNDDOWN extends Function {
    static final String NAME = ROUNDDOWN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        double number = NumberPool.DOUBLE_0;
        long digit = NumberPool.LONG_6;

        if (args[0] != null) {
            number = DataUtil.getNumberValue(args[0]).doubleValue();
        }
        if (args[1] != null) {
            digit = DataUtil.getNumberValue(args[1]).longValue();
        }

        return new BigDecimal(DataUtil.number2String(number)).setScale((int) digit, BigDecimal.ROUND_DOWN).doubleValue();
    }
}

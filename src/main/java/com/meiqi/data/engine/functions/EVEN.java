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
public final class EVEN extends Function {
    static final String NAME = EVEN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            double d = DataUtil.getNumberValue(args[0]).doubleValue();
            int cmp = DataUtil.compare(d, 0D);

            if (cmp == 0) {
                return 0;
            } else if (d < 0) { // 负的
                d = Math.abs(d);
                int iValue = (int) d;

                if (DataUtil.compare(iValue, d) == 0) { // 本身就是整数
                    if (iValue % 2 == 0) { // 偶数
                        //
                    } else {
                        iValue += 1;
                    }
                } else {
                    if (iValue % 2 == 0) {
                        iValue += 2;
                    } else {
                        iValue += 1;
                    }
                }

                return new Long(0 - iValue);
            } else {
                int iValue = (int) d;

                if (DataUtil.compare(iValue, d) == 0) { // 本身就是整数
                    if (iValue % 2 == 0) { // 偶数
                        //
                    } else {
                        iValue += 1;
                    }
                } else {
                    if (iValue % 2 == 0) {
                        iValue += 2;
                    } else {
                        iValue += 1;
                    }
                }

                return new Long(iValue);
            }
        }

        throw new ArgsCountError(NAME);
    }
}

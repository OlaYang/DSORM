package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;

/**
 * User: 
 * Date: 13-7-17
 * Time: 下午6:30
 */
public class AVERAGE extends Function {
    static final String NAME = AVERAGE.class.getSimpleName();
    static final Function SUM_F = getFunction("SUM");
    static final Function COUNT_F = getFunction("COUNT");


    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        Object sumO = SUM_F.eval(calInfo, args);
        Object countO = COUNT_F.eval(calInfo, args);

        double count = DataUtil.getNumberValue(countO).doubleValue();
        double sum = DataUtil.getNumberValue(sumO).doubleValue();

        if (DataUtil.compare(count, NumberPool.DOUBLE_0) == 0) {
            return NumberPool.LONG_0;
        }

        return sum / count;
    }
}

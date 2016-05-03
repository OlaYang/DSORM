package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4_O_;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;

import java.util.Map;

/**
 * User: 
 * Date: 13-7-11
 * Time: 上午9:29
 * <hr>分组求和</hr>
 */
public class _O_MAXBYPARA extends Function {
    public static final String NAME = _O_MAXBYPARA.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        final String serviceName = DataUtil.getServiceName(args[0]);
        final String colCal = DataUtil.getStringValue(args[1]);

        final Map<String, Object> currentParam = getParam(args, 2, calInfo.getParam(), true);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);

        Double sum = (Double) cache.get(colCal);
        if (sum == null) {
            sum = _O_MAX.init(calInfo, serviceName, currentParam, colCal, NAME);
            cache.put(colCal, sum);
        }

        return sum;
    }
}
package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import java.util.Map;

/**
 * User: 
 * Date: 13-7-11
 * Time: 下午1:55
 * <hr> 计数 </hr>
 */
public class _O_COUNT extends Function {
    public static final String NAME = _O_COUNT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }


        final String serviceName = DataUtil.getServiceName(args[0]);
        final String colCal = DataUtil.getStringValue(args[1]);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, calInfo.getParam(), NAME);

        Long result = (Long) cache.get(colCal);
        if (result == null) {
            result = init(calInfo, serviceName, calInfo.getParam(), colCal, NAME);
            cache.put(colCal, result);
        }

        return result;
    }

    static long init(CalInfo calInfo, String serviceName, Map<String, Object> currentParam
            , String colCal, String funcName) throws RengineException {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }

        D2Data d2Data = Cache4D2Data.getD2Data(servicePo, currentParam,
                calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);

        long sum = NumberPool.LONG_0;
        final int colCalInt = DataUtil.getColumnIntIndex(colCal, d2Data.getColumnList());

        if (colCalInt == -1) {
            throw new ArgColumnNotFound(funcName, colCal);
        }

        final Object[][] value = d2Data.getData();

        for (int i = 0; i < value.length; i++) {
            final Object colCalValue = value[i][colCalInt];
            if (colCalValue == null) {
                continue;
            }

            sum++;
        }

        return sum;
    }


}

package com.meiqi.data.engine.functions._O;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;
import com.meiqi.data.po.TServicePo;

import java.util.HashMap;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-22
 * Time: 下午1:12
 */
public class _O_GETBYPARA2 extends Function {
    public static final String NAME = _O_GETBYPARA2.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        final String serviceName = DataUtil.getServiceName(args[0]);
        final String reqKey = DataUtil.getStringValue(args[1]);


        final Map<String, Object> currentParam = getParam(args, 2, calInfo.getParam(), true);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
        boolean isColumnIndex = DataUtil.isColumnIndex(reqKey);
        Map<String, Object> trueCache = (Map<String, Object>) cache.get(isColumnIndex);

        if (trueCache == null) {
            trueCache = new HashMap<String, Object>();
            TService servicePo = Services.getService(serviceName);
            if (servicePo == null) {
                throw new ServiceNotFound(serviceName);
            }

            final D2Data d2Data =
                    Cache4D2Data.getD2Data(servicePo, currentParam, calInfo.getCallLayer()
                            , calInfo.getServicePo(), calInfo.getParam(), NAME);

            if (d2Data.getData().length > 1) {
                throw new RengineException(serviceName, NAME + "结果集多于一行, 行数为:" + d2Data.getData().length
                        + ", 参数为:" + JSON.toJSONString(currentParam));
            }

            for (TServiceColumn columnPo : d2Data.getColumnList()) {
                Object value = d2Data.getValue(columnPo.getColumnName(), 0);

                if (value == null) {
                    value = StringPool.EMPTY;
                }

                trueCache.put(isColumnIndex ? columnPo.getColumnIndex() : columnPo.getColumnName()
                        , value);
            }

            cache.put(isColumnIndex, trueCache);
        }

        Object result = trueCache.get(reqKey);

        if (result == null) {
            throw new RengineException(calInfo.getServiceName(), NAME + "列未找到, " + reqKey);
        }

        return result;
    }


}

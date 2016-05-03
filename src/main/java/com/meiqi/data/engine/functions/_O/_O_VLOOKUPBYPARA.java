package com.meiqi.data.engine.functions._O;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4_O_;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.util.LogUtil;

import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-16
 * Time: 上午9:46
 */
public class _O_VLOOKUPBYPARA extends Function {
    public static final String NAME = _O_VLOOKUPBYPARA.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 5) {
            throw new ArgsCountError(NAME);
        }

        final String reqKey = DataUtil.getStringValue(args[0]);
        final String serviceName = DataUtil.getServiceName(args[1]);
        final String colBy = DataUtil.getStringValue(args[2]);
        final String colCal = DataUtil.getStringValue(args[3]);
        boolean rangLookup = false; // 精确

        if (args[4] instanceof Boolean) {
            rangLookup = (Boolean) args[4];
        }


        final Map currentParam = getParam(args, 5, calInfo.getParam(), true);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
        _O_VLOOKUP.__Key key = new _O_VLOOKUP.__Key(colBy, colCal, rangLookup);

        Object result = cache.get(key);

        if (result == null) {
            if (!rangLookup) {
                result = _O_VLOOKUP.init(calInfo, serviceName, currentParam, colBy, colCal, NAME);
                cache.put(key, result);
            } else {
                result = _O_VLOOKUP.init1(calInfo, serviceName, currentParam, colBy, colCal, NAME);
                cache.put(key, result);
            }
        }

        Object ret = null;

        if (!rangLookup) {
            ret = ((Map<String, Object>) result).get(reqKey);
        } else {
            _O_VLOOKUP.Row tmp = new _O_VLOOKUP.Row();
            if (args[0] instanceof Number) {
                tmp.key = ((Number) args[0]).doubleValue();
            } else {
                tmp.key = reqKey;
            }

            List<_O_VLOOKUP.Row> lstResult = ((List<_O_VLOOKUP.Row>) result);
            for (int i = 0; i < lstResult.size(); i++) {
                if (tmp.compareTo(lstResult.get(i)) < 0) {
                    if (i > 0) {
                        ret = lstResult.get(i - 1).value;
                    }
                    break;
                } else if (tmp.compareTo(lstResult.get(i)) == 0) {
                    ret = lstResult.get(i).value;
                    break;
                }

                if (i == lstResult.size() - 1) {
                    ret = lstResult.get(i).value;
                    break;
                }
            }
        }

        if (ret == null) {
            return StringPool.EMPTY;
        }

        return ret;
    }
}

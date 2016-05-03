package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.engine.functions.CriteriaUtil;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import java.util.HashMap;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-11
 * Time: 下午1:55
 * <hr> 分组计数 </hr>
 */
public class _O_COUNTIFBYGROUP extends Function {
    public static final String NAME = _O_COUNTIFBYGROUP.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 5) {
            throw new ArgsCountError(NAME);
        }

        final String reqKey = DataUtil.getStringValue(args[0]);
        final String serviceName = DataUtil.getServiceName(args[1]);
        final String colBy = DataUtil.getStringValue(args[2]);
        final String colCal = DataUtil.getStringValue(args[3]);
        final String criteria = DataUtil.getStringValue(args[4]);

        final Map<String, Object> param = getParam(args, 5, calInfo.getParam(), false);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, param, NAME);
        __Key key = new __Key(colBy, colCal, criteria);

        Map<String, Long> result = (Map<String, Long>) cache.get(key);
        if (result == null) {
            result = init(calInfo, serviceName, param, colBy, colCal, criteria);
            cache.put(key, result);
        }

        Long ret = result.get(reqKey);
        if (ret == null) {
            return NumberPool.LONG_0;
        }
        return ret;
    }

    class __Key {
        Object colBy, colCal, criteria;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colBy != null ? !colBy.equals(key.colBy) : key.colBy != null)
                return false;
            if (colCal != null ? !colCal.equals(key.colCal) : key.colCal != null)
                return false;
            if (criteria != null ? !criteria.equals(key.criteria) : key.criteria != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colBy != null ? colBy.hashCode() : 0;
            result = 31 * result + (colCal != null ? colCal.hashCode() : 0);
            result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
            return result;
        }

        __Key(Object colBy, Object colCal, Object criteria) {
            this.colBy = colBy;
            this.colCal = colCal;
            this.criteria = criteria;
        }
    }

    private Map<String, Long> init(CalInfo calInfo, String serviceName
            , Map<String, Object> param, String colBy, String colCal, String criteria)
            throws RengineException {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }

        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, param, calInfo.getCallLayer()
                        , calInfo.getServicePo(), calInfo.getParam(), NAME);

        Map<String, Long> result = new HashMap<String, Long>();
        final Object[][] value = d2Data.getData();
        int colByInt = DataUtil.getColumnIntIndex(colBy, d2Data.getColumnList());
        int colCalInt = DataUtil.getColumnIntIndex(colCal, d2Data.getColumnList());

        if (colByInt == -1) {
            throw new ArgColumnNotFound(NAME, colBy);
        }
        if (colCalInt == -1) {
            throw new ArgColumnNotFound(NAME, colCal);
        }

        CriteriaUtil.I_MatchPredicate m = CriteriaUtil.createCriteriaPredicate((criteria));
        for (int i = 0; i < value.length; i++) {
            final Object colByValue = value[i][colByInt];
            final Object colCalValue = value[i][colCalInt];
            if (colByValue == null
                    || !m.matches(DataUtil.getValueEval(colCalValue))) {
                continue;
            }

            String key = DataUtil.getStringValue(colByValue);
            Long sum = result.get(key);

            if (sum == null) {
               // result.put(key, NumberPool.LONG_0);
                result.put(key, NumberPool.LONG_1); // bug,llcheng,计数少了一个
            } else {
                result.put(key, sum + NumberPool.LONG_1);
            }
        }

        return result;
    }

}

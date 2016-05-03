package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import java.util.*;

/**
 * User: 
 * Date: 13-7-16
 * Time: 下午3:37
 */
public class _O_PERCENTINMAX extends Function {
    public static final String NAME = _O_PERCENTINMAX.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 4) {
            throw new ArgsCountError(NAME);
        }

        boolean isPassZero = false;
        if (args.length > 4 && args[4] instanceof Boolean) {
            isPassZero = (Boolean) args[4];
        }

        final String reqKey = DataUtil.getStringValue(args[0]);
        final String serviceName = DataUtil.getServiceName(args[1]);
        final String colBy = DataUtil.getStringValue(args[2]);
        final String colCal = DataUtil.getStringValue(args[3]);

        final Map<String, Object> param = getParam(args, 5, calInfo.getParam(), false);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, param, NAME);
        __Key key = new __Key(colBy, colCal, isPassZero);
        Map<String, Double> result = (Map<String, Double>) cache.get(key);
        if (result == null) {
            result = init(calInfo, serviceName, param, colBy, colCal, isPassZero);
            cache.put(key, result);
        }

        Double ret = result.get(reqKey);
        if (ret == null) {
            return NumberPool.DOUBLE_0;
        }
        return ret;
    }


    class __Key {
        Object colBy, colCal, isPassZero;

        __Key(Object colBy, Object colCal, Object passZero) {
            this.colBy = colBy;
            this.colCal = colCal;
            isPassZero = passZero;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colBy != null ? !colBy.equals(key.colBy) : key.colBy != null)
                return false;
            if (colCal != null ? !colCal.equals(key.colCal) : key.colCal != null)
                return false;
            if (isPassZero != null ? !isPassZero.equals(key.isPassZero) : key.isPassZero != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colBy != null ? colBy.hashCode() : 0;
            result = 31 * result + (colCal != null ? colCal.hashCode() : 0);
            result = 31 * result + (isPassZero != null ? isPassZero.hashCode() : 0);
            return result;
        }
    }

    private Map<String, Double> init(CalInfo calInfo, String serviceName
            , Map<String, Object> param, String colBy, String colCal, boolean isPassZero) throws RengineException, CalculateError {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }

        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, param, calInfo.getCallLayer()
                        , calInfo.getServicePo(), calInfo.getParam(), NAME);

        Map<String, Double> result = new HashMap<String, Double>();
        final Object[][] value = d2Data.getData();
        int colByInt = DataUtil.getColumnIntIndex(colBy, d2Data.getColumnList());
        int colCalInt = DataUtil.getColumnIntIndex(colCal, d2Data.getColumnList());

        if (colByInt == -1) {
            throw new ArgColumnNotFound(NAME, colBy);
        }
        if (colCalInt == -1) {
            throw new ArgColumnNotFound(NAME, colCal);
        }

        List<Double> values = new ArrayList<Double>();

        for (int i = 0; i < value.length; i++) {
            final Object colByValue = value[i][colByInt];
            final Object colCalValue = value[i][colCalInt];
            if (colByValue == null) {
                continue;
            }

            if (canNumberOP(colCalValue)) {
                String __key = DataUtil.getStringValue(colByValue);
                double __value = DataUtil.getNumberValue(colCalValue).doubleValue();

                if (DataUtil.compare(__value, 0D) == 0 && isPassZero) {
                    continue;
                }

                values.add(__value);
                result.put(__key, __value);
            }
        }

        Collections.sort(values);

        for (Map.Entry<String, Double> entry : result.entrySet()) {
            Double value_ = entry.getValue();
            if (DataUtil.compare(value_, 0D) == 0) {
                result.put(entry.getKey(), 0D);
                continue;
            }

            boolean isSet = false;
            int countLTCur = 0;

            for (int i = 0; i < values.size(); i++) {
                if (DataUtil.compare(values.get(i), 0D) == 0) {
                    continue;
                }

                if (DataUtil.compare(values.get(i), value_) >= NumberPool.LONG_0) {
                    result.put(entry.getKey(), (countLTCur + 1D) / values.size());
                    isSet = true;
                    break;
                }

                countLTCur++;
            }

            if (!isSet) {
                result.put(entry.getKey(), 1D);
            }
        }

        return result;
    }

}

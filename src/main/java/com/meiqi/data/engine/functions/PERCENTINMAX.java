package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.*;

/**
 * User: 
 * Date: 13-7-17
 * Time: 下午7:27
 */
public class PERCENTINMAX extends Function {
    static final String NAME = PERCENTINMAX.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        if (args[1] instanceof ExcelRange) {
            double currentValue = DataUtil.getNumberValue(args[0]).doubleValue();
            ExcelRange range = (ExcelRange) args[1];

            boolean isPassZero = false;
            if (args.length > 2 && args[2] instanceof Boolean) {
                isPassZero = (Boolean) args[2];
            }

            __Key key = new __Key(range, isPassZero);
            Map<Object, Object> cache = calInfo.getCache(NAME);
            List<Double> result = (List<Double>) cache.get(key);

            if (result == null) {
                result = new ArrayList<Double>();

                Iterator<Object> ite = range.getIterator();

                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    if (tmp != null && canNumberOP(tmp)) {
                        double tmpD = DataUtil.getNumberValue(tmp).doubleValue();

                        if (isPassZero
                                && DataUtil.compare(tmpD, NumberPool.DOUBLE_0) == 0) {
                            continue;
                        }

                        result.add(tmpD);
                    }
                }

                Collections.sort(result);
                cache.put(key, result);
            }

            final int size = result.size();

            if (size == 0 || DataUtil.compare(currentValue, NumberPool.DOUBLE_0) == 0) {
                return NumberPool.DOUBLE_0;
            }

            long countLTCur = NumberPool.LONG_0;

            for (int i = 0; i < size; i++) {
                if (DataUtil.compare(result.get(i), 0D) == 0) {
                    continue;
                }

                if (result.get(i) >= currentValue) {
                    return (countLTCur + NumberPool.DOUBLE_1) / size;
                }

                countLTCur++;
            }

            return NumberPool.DOUBLE_1;
        }


        throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");

    }

    class __Key {
        Object colCal, isPassZero;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colCal != null ? !colCal.equals(key.colCal) : key.colCal != null)
                return false;
            if (isPassZero != null ? !isPassZero.equals(key.isPassZero) : key.isPassZero != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colCal != null ? colCal.hashCode() : 0;
            result = 31 * result + (isPassZero != null ? isPassZero.hashCode() : 0);
            return result;
        }

        __Key(Object colCal, Object passZero) {
            this.colCal = colCal;
            isPassZero = passZero;
        }
    }

}

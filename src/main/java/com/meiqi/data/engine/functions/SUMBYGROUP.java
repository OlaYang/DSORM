package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-11
 * Time: 上午9:29
 * <hr>分组求和</hr>
 * 分组依据列的值     e.g. 1<hr/>
 * 分组依据的列             e.g. H<hr/>
 * SUM的列                       e.g. I<hr/>
 * 依据的数据源                          e.g. 2<hr/>
 */
public class SUMBYGROUP extends Function {
    public static final String NAME = SUMBYGROUP.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 3) {
            throw new ArgsCountError(NAME);
        }

        final String reqKey = DataUtil.getStringValue(args[0]);
        if (!(args[1] instanceof ExcelRange) || !(args[2] instanceof ExcelRange)) {
            throw new RengineException(calInfo.getServiceName(), "输入不是数列");
        }

        final ExcelRange colBy = (ExcelRange) args[1];
        final ExcelRange colSum = (ExcelRange) args[2];

        Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(colBy, colSum);
        Map<String, Double> result = (Map<String, Double>) cache.get(key);

        if (result == null) {
            result = new HashMap<String, Double>();

            Iterator<Object> iteSum = colSum.getIterator();
            Iterator<Object> iteBy = colBy.getIterator();

            while (true) {

                if (iteSum.hasNext() && iteBy.hasNext()) {
                    Object sumT = iteSum.next();
                    String byT = DataUtil.getStringValue(iteBy.next());

                    if (canNumberOP(sumT)) {
                        Double tmpD = result.get(byT);
                        if (tmpD == null) {
                            result.put(byT, DataUtil.getNumberValue(sumT).doubleValue());
                        } else {
                            result.put(byT, tmpD + DataUtil.getNumberValue(sumT).doubleValue());
                        }
                    }
                } else {
                    break;
                }
            }

            cache.put(key, result);
        }

        Double ret = result.get(reqKey);
        if (ret == null) {
            return NumberPool.LONG_0;
        }

        return ret;
    }

    class __Key {
        Object colBy, colSum;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colBy != null ? !colBy.equals(key.colBy) : key.colBy != null)
                return false;
            if (colSum != null ? !colSum.equals(key.colSum) : key.colSum != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colBy != null ? colBy.hashCode() : 0;
            result = 31 * result + (colSum != null ? colSum.hashCode() : 0);
            return result;
        }

        __Key(Object colBy, Object colSum) {
            this.colBy = colBy;
            this.colSum = colSum;
        }
    }


}
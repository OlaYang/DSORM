package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4_O_;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-11
 * Time: 下午1:55
 * <hr> 分组计数 </hr>
 */
public class _O_SUMIFSBYPARA extends Function {
    public static final String NAME = _O_SUMIFSBYPARA.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        final String serviceName = DataUtil.getServiceName(args[0]);
        final String colSum = DataUtil.getStringValue(args[1]);
        final List<String> colCals = new ArrayList<String>();
        final List<String> criterias = new ArrayList<String>();

        int flagIndex = -1;

        for (int i = 2; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                if (flagIndex == -1) {
                    throw new RengineException(calInfo.getServiceName(), NAME + "未找到分隔符|-|");
                }
                break;
            }

            String colCal = DataUtil.getStringValue(args[i]);
            String criterial = DataUtil.getStringValue(args[i + 1]);
            if (colCal.equals("|") && criterial.equals("|")) {
                flagIndex = i + 1;
                break;
            }

            colCals.add(colCal);
            criterias.add(criterial);
        }
        Map<String, Object> current = DataUtil.EMPTY;

        if (flagIndex != -1) {
            current = getParam(args, flagIndex + 1, calInfo.getParam(), true);
        }

        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, current, NAME);
        __Key key = new __Key(colCals, colSum, criterias);

        Double result = (Double) cache.get(key);
        if (result == null) {
            result = _O_SUMIFS.init(calInfo, serviceName, current, colCals, criterias, colSum, NAME);
            cache.put(key, result);
        }

        return result;
    }


    class __Key {
        Object colCals, colSum, criterias;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colCals != null ? !colCals.equals(key.colCals) : key.colCals != null)
                return false;
            if (colSum != null ? !colSum.equals(key.colSum) : key.colSum != null)
                return false;
            if (criterias != null ? !criterias.equals(key.criterias) : key.criterias != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colCals != null ? colCals.hashCode() : 0;
            result = 31 * result + (colSum != null ? colSum.hashCode() : 0);
            result = 31 * result + (criterias != null ? criterias.hashCode() : 0);
            return result;
        }

        __Key(Object colCals, Object colSum, Object criterias) {
            this.colCals = colCals;
            this.colSum = colSum;
            this.criterias = criterias;
        }
    }
}

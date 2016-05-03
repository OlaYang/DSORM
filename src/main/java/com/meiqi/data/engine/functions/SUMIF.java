package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.Iterator;

/**
 * User: 
 * Date: 13-7-17
 * Time: 下午6:30
 */
public class SUMIF extends Function {
    static final String NAME = SUMIF.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        if (args[0] instanceof ExcelRange) {
            ExcelRange rangeCriteria = (ExcelRange) args[0];
            String criteria = DataUtil.getStringValue(args[1]);
            ExcelRange rangeSum = null;

            if (args.length > 2 && args[2] instanceof ExcelRange) {
                rangeSum = (ExcelRange) args[2];
            }


            double sum = NumberPool.DOUBLE_0;

            Iterator<Object> iteCriteria = rangeCriteria.getIterator();
            Iterator<Object> iteSum = null;

            if (rangeSum != null) {
                iteSum = rangeSum.getIterator();
            }

            CriteriaUtil.I_MatchPredicate mp = CriteriaUtil.createCriteriaPredicate((criteria));

            while (iteCriteria.hasNext()) {
                Object tmp = iteCriteria.next();
                Object addSum = tmp;

                if (iteSum != null) {
                    if (iteSum.hasNext()) {
                        addSum = iteSum.next();
                    } else {
                        break;
                    }
                }

                if (!mp.matches(DataUtil.getValueEval(tmp))) {
                    continue;
                }

                if (canNumberOP(addSum)) {
                    sum += DataUtil.getNumberValue(addSum).doubleValue();
                }
            }

            return sum;
        }

        throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
    }

    class __Key {
        Object colCriteria, criteria, colSum;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colCriteria != null ? !colCriteria.equals(key.colCriteria) : key.colCriteria != null)
                return false;
            if (colSum != null ? !colSum.equals(key.colSum) : key.colSum != null)
                return false;
            if (criteria != null ? !criteria.equals(key.criteria) : key.criteria != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colCriteria != null ? colCriteria.hashCode() : 0;
            result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
            result = 31 * result + (colSum != null ? colSum.hashCode() : 0);
            return result;
        }

        __Key(Object colCriteria, Object criteria, Object colSum) {
            this.colCriteria = colCriteria;
            this.colSum = colSum;
            this.criteria = criteria;
        }
    }
}

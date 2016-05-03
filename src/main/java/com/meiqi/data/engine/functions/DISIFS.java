package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.ExcelRange;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 
 * Date: 13-7-17
 * Time: 下午6:30
 */
public class DISIFS extends Function {
    static final String NAME = DISIFS.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 2 || args.length % 2 != 0) {
            throw new ArgsCountError(NAME);
        }

        if (args[1] instanceof ExcelRange) {
            final String reqKey = DataUtil.getStringValue(args[0]);
            ExcelRange range = (ExcelRange) args[1];

            int currentIndex = calInfo.getCurRow();
            int lastIndex = currentIndex;
            int currentColumnIndex = range.getX1();

            final List<ExcelRange> areas = new ArrayList<ExcelRange>();
            final List<String> criterias = new ArrayList<String>();

            for (int i = 2; i < args.length; i += 2) {
                if (args[i] instanceof ExcelRange) {
                    areas.add((ExcelRange) args[i]);
                    criterias.add(DataUtil.getStringValue(args[i + 1]));
                } else {
                    throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
                }
            }

            CriteriaUtil.I_MatchPredicate[] mp = new CriteriaUtil.I_MatchPredicate[criterias.size()];
            for (int i = 0; i < criterias.size(); i++) {
                mp[i] = CriteriaUtil.createCriteriaPredicate((criterias.get(i)));
            }

            for (int i = lastIndex - 1; i >= 0; i--) {
                boolean isOK = true;
                for (int size_j = 0; size_j < areas.size(); size_j++) {
                    final ExcelRange area = areas.get(size_j);
                    if (!mp[size_j].matches(DataUtil.getValueEval(area.getValue(area.getX1(), i)))) {
                        isOK = false;
                        break;
                    }
                }

                if (isOK
                        && DataUtil.getStringValue(range.getValue(currentColumnIndex, i))
                        .equalsIgnoreCase(reqKey)) {
                    lastIndex = i;
                    break;
                }
            }

            return new Long((currentIndex - lastIndex));
        }

        throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
    }
}

package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.ExcelRange;

/**
 * User: 
 * Date: 13-7-22
 * Time: 下午1:12
 */
public class DIS extends Function {
    static final String NAME = DIS.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        if (args[1] instanceof ExcelRange) {
            final String reqKey = DataUtil.getStringValue(args[0]);
            ExcelRange range = (ExcelRange) args[1];

            int currentIndex = calInfo.getCurRow();
            int lastIndex = currentIndex;
            int currentColumnIndex = range.getX1();

            for (int i = lastIndex - 1; i >= 0; i--) {
                if (DataUtil.getStringValue(range.getValue(currentColumnIndex, i)).equalsIgnoreCase(reqKey)) {
                    lastIndex = i;
                    break;
                }
            }

            return new Long((currentIndex - lastIndex));
        }

        throw new RengineException(calInfo.getServiceName(), NAME+ "输入不是数列");
    }

}
